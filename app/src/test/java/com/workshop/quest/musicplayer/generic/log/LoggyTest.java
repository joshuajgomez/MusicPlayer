package com.workshop.quest.musicplayer.generic.log;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LoggyTest {

    @InjectMocks
    private Loggy mLoggy;

    @Before
    public void setUp() throws Exception {
        Loggy.entryLog();
    }
}