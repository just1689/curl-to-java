package codeexecutor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlToClassNameTest {

    @Test
    public void urlToClassNameTestWithParams() {
        String url = "https://jsonplaceholder.typicode.com/users?123=";
        String className = UrlToClassName.urlToClassName(url);
        assertEquals("JsonplaceholderTypicodeComUsers", className);
    }

    @Test
    public void urlToClassNameTest() {
        String url = "https://jsonplaceholder.typicode.com/users";
        String className = UrlToClassName.urlToClassName(url);
        assertEquals("JsonplaceholderTypicodeComUsers", className);
    }

    @Test
    public void urlToClassNameTestWithDashes() {
        String url = "https://jsonplace-holder.typicode.com/users";
        String className = UrlToClassName.urlToClassName(url);
        assertEquals("JsonplaceHolderTypicodeComUsers", className);
    }

    @Test
    public void urlToClassNameTestHttp() {
        String url = "http://jsonplaceholder.typicode.com/users";
        String className = UrlToClassName.urlToClassName(url);
        assertEquals("JsonplaceholderTypicodeComUsers", className);
    }

}