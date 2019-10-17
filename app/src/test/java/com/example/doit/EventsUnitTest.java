package com.example.doit;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Event local unit test, which will execute on the development machine (host).
 *
 */
public class EventsUnitTest {
    //test if event info are accessible.
    @Test
    public void event_getInfo_test(){
        Event event=new Event("cook","2019-10-10","12:00","make salad","anu",false);
        assertEquals("cook",event.getEventName());
        assertEquals("2019-10-10",event.getDate());
        assertEquals("12:00",event.getTime());
        assertEquals("make salad",event.getDescription());
        assertEquals("anu",event.getLocation());
        assertEquals(false,event.getComplete());
    }
    //test if event can be edited successfully.
    @Test
    public void event_edit_test(){
        Event event=new Event("cook","2019-10-10","12:00","make salad","anu",false);
        event.setEventName("wash");
        assertEquals("wash",event.getEventName());
        event.setDate("2019-10-11");
        assertEquals("2019-10-11",event.getDate());
        event.setTime("13:00");
        assertEquals("13:00",event.getTime());
        event.setDescription("wash the dishes");
        assertNotEquals("make salad",event.getDescription());
        assertEquals("wash the dishes",event.getDescription());
        event.setLocation("paris");
        assertEquals("paris",event.getLocation());
        event.setComplete(true);
        assertNotEquals(false,event.getComplete());
        assertEquals(true,event.getComplete());
    }
    //test the basic operations in event list
    @Test
    public void event_list_operation_test(){
        Event event1=new Event("cook","2019-10-10","12:00","make salad","anu",false);
        Event event2=new Event("wash","2019-10-11","13:00","wash the dishes","paris",true);
        ArrayList<Event> event_list=new ArrayList<>();
        event_list.add(event1);
        assertTrue("adding method doesn't work. " ,!event_list.isEmpty());
        event_list.remove(0);
        assertFalse("remove function doesn't work",!event_list.isEmpty());
        event_list.add(event1);
        event_list.add(event2);
        assertEquals(2,event_list.size());
        event2.setComplete(false);
        assertEquals(false,event_list.get(1).getComplete());
    }
}