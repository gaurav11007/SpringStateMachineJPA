package com.statemachine.statemachinejpapersistanceexample.config;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;
import com.statemachine.statemachinejpapersistanceexample.enums.Events;
import com.statemachine.statemachinejpapersistanceexample.enums.States;



@Configuration
@EnableStateMachineFactory
public class Config1 extends EnumStateMachineConfigurerAdapter<States, Events> { 
	
	@Autowired
	private JpaStateMachineRepository jpaStateMachineRepository;

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
		.withPersistence()
			.runtimePersister(stateMachineRuntimePersister());
    }
    

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
		        .withStates()
		        .initial(States.I)
		        .choice(States.STAGE_CHOICE1)
		        .choice(States.STAGE_CHOICE2)
        		.states(EnumSet.allOf(States.class));
    }
    
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
              .withExternal()
                .source(States.I).target(States.A).event(Events.E1)
               	.and()
              .withExternal()
                .source(States.A).target(States.STAGE_CHOICE1).event(Events.E2).action(action_STAGE_CHOICE1())
                .and()
		      .withChoice()
		        .source(States.STAGE_CHOICE1)
		        .first(States.B, guardB())
		        .then(States.STAGE_CHOICE2, guard_STAGE_CHOICE2(),action_STAGE_CHOICE2())
		        .then(States.C, guardC())
		        // .last(States.A)
		        .and()
		       .withChoice()
  		        .source(States.STAGE_CHOICE2)
  		        .first(States.B, guardB())
  		        .then(States.D, guardD());
  		        //.last(States.A);
    }
 
    


	@Bean
	public StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister() {
		return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}
    
    @Bean
	public StateMachineService<States, Events> stateMachineService(StateMachineFactory<States, Events> stateMachineFactory,
			StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister) {
		return new DefaultStateMachineService<States, Events>(stateMachineFactory, stateMachineRuntimePersister);
	}
    
    
    public Action<States, Events> action_STAGE_CHOICE1() {
    	return (context) -> {
			System.out.println("Inside STAGE_CHOICE1 action" );
			context.getExtendedState().getVariables().put("RESULT", "STAGE_CHOICE2");
		};
    }
    
    public Action<States, Events> action_STAGE_CHOICE2() {
    	return (context) -> {
    		System.out.println("Inside STAGE_CHOICE1 action");
    		context.getExtendedState().getVariables().put("RESULT", "D");
		};
    }
    
    public Guard<States, Events> guardB() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	if ( null != context.getExtendedState().getVariables().get("RESULT"))
            		if ("B".equals(context.getExtendedState().getVariables().get("RESULT")))
            			return true;
                return false;
            }
        };
    }
    
    public Guard<States, Events> guard_STAGE_CHOICE2() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	if ( null != context.getExtendedState().getVariables().get("RESULT"))
            		if ("STAGE_CHOICE2".equals(context.getExtendedState().getVariables().get("RESULT")))
            			return true;
                return false;
            }
        };
    }
    public Guard<States, Events> guardC() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	if ( null != context.getExtendedState().getVariables().get("RESULT"))
            		if ("C".equals(context.getExtendedState().getVariables().get("RESULT")))
            			return true;
                return false;
            }
        };
    }
    
    public Guard<States, Events> guardD() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	if ( null != context.getExtendedState().getVariables().get("RESULT"))
            		if ("D".equals(context.getExtendedState().getVariables().get("RESULT")))
            			return true;
                return false;
            }
        };
    }
    
	
	 @Bean
	    public StateMachineListener<States, Events> listener() {
	
	        return new StateMachineListenerAdapter<States, Events>() {
	            @Override
	            public void stateChanged(State<States, Events> from, State<States, Events> to) {
	            	System.out.println("Listerner : In state chnaged");
	                if (from == null) {
	                    System.out.println("State machine initialised in state " + to.getId());
	                } else {
	                    System.out.println("State changed from " + from.getId() + " to " + to.getId());
	                }
	            }
	        };
	    }

	

}