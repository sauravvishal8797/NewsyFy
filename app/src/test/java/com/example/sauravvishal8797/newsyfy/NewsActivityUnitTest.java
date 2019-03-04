package com.example.sauravvishal8797.newsyfy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewsActivityUnitTest {

    @Mock
    NewsActivity newsActivity;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadUrlTest(){
        newsActivity.loadUrl("google.com");
    }
}
