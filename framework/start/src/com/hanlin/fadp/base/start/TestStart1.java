package com.hanlin.fadp.base.start;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestStart1 {
	class Person{
		private String name;
		private String home;
		Person(String name,String home){
			this.name=name;
			this.home=home;
		}
		public String getName() {
			return name;
		}
		public String getHome() {
			return home;
		}
		
	}
    public enum ServerState {
        STARTING, RUNNING, STOPPING;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
    
    public static void main(String[] args){
    	ServerState[] serverStates=ServerState.values();
    	for(ServerState state:serverStates){
    		String name=state.name();
    		System.out.println(name);
    	}
    	System.out.println(ServerState.RUNNING.toString());
    	
    	//
    	TestStart1 st=new TestStart1();
    	Map<String,Person> persons=new LinkedHashMap<String,Person>();
    	Person person1= st.new Person("zhang","hubei");
    	Person person2= st.new Person("zhang","hubei1");
    	persons.put(person1.getName(), person1);
    	persons.put(person2.getName(),person2);
    	Iterator<Map.Entry<String,Person>> me=persons.entrySet().iterator();
    	while(me.hasNext()){
    		Map.Entry<String, Person> it=me.next();
    		String name=it.getKey();
    		Person person=it.getValue();
    		System.out.println(name+":"+person.getHome());
    	}
    }
}
