package com.statemachine.statemachinejpapersistanceexample.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.statemachine.statemachinejpapersistanceexample.enums.Events;
import com.statemachine.statemachinejpapersistanceexample.enums.States;




@RestController
@RequestMapping("/StateMachine")
public class StateMachineController {
	
	StateMachine<States, Events> stateMachine;
	
	@Autowired
	private StateMachineService<States, Events> stateMachineService;
	
	@RequestMapping(value = "/init",method = RequestMethod.POST)
    public void init(@RequestBody Map<String, String> parameters) {
		System.out.println("Inside of StateMachine Controller : INIT");
		
		try {
			// Get New StateMachine
			stateMachine = getStateMachine(parameters.get("guid"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("StateMachine Initialized To State :" + stateMachine.getState().toString());
		
		
	}
	
	@RequestMapping(value = "/proceed",method = RequestMethod.POST)
	public void proceed(@RequestBody Map<String, String> parameters) {
		System.out.println("Inside of  StateMachine Controller : PROCEED ");
		try {
			// Get New StateMachine
			stateMachine = getStateMachine(parameters.get("guid"));
			System.out.println("StateMachine Reset and Started To State :" + stateMachine.getState().toString());
			// Sending Event
			stateMachine.sendEvent(Events.valueOf(parameters.get("event")));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("StateMachine Proceeded To State :" + stateMachine.getState().toString());
	}
	
	// Synchronized method to obtain persisted SM from Database.
	private synchronized StateMachine<States, Events> getStateMachine(String machineId) throws Exception {
		if (stateMachine == null) {
			stateMachine = stateMachineService.acquireStateMachine(machineId);
			stateMachine.start();
		} else if (!ObjectUtils.nullSafeEquals(stateMachine.getId(), machineId)) {
			stateMachineService.releaseStateMachine(stateMachine.getId());
			stateMachine.stop();
			stateMachine = stateMachineService.acquireStateMachine(machineId);
			stateMachine.start();
		}
		return stateMachine;
	}
}
