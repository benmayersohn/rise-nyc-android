package com.therise.nyc.therisenyc;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutTests {
    @Test
    public void millisToTimeString_isCorrect() throws Exception {
        assertEquals("0:35", TimeTools.millisToTimeString(35000));
        assertEquals("1:45", TimeTools.millisToTimeString(105000));
    }

    @Test
    public void getDistanceFromSecondMark_isCorrect() throws Exception{
        long timeElapsed = 3500;
        long numSecsElapsed = (long)Math.floor(timeElapsed / WorkoutStatic.ONE_SECOND);
        System.out.println(Long.toString(numSecsElapsed));
        assertEquals(500, timeElapsed - numSecsElapsed * WorkoutStatic.ONE_SECOND);
    }

    // the next test confirms that overridden methods are selected, even when the container
    // is of the superclass type

    class Animal{
        String getSound(){
            return "No sound!";
        }
    }

    private class Dog extends Animal{
        @Override
        String getSound(){
            return "WOOF!";
        }
    }

    @Test
    public void overriddenMethod_isSuccessful() throws Exception{
        Animal a = new Dog();
        assertEquals("WOOF!",a.getSound());
    }
}