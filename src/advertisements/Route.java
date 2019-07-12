/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advertisements;

/**
 *
 * @author Ammar
 */
public enum Route {
    LOGIN("login"),
    REGISTER("register"),
    REQUEST_ADVERTISEMENTS("advertisements/request"),
    BROADCASTING("broadcasting/auth");
    
    private final String route;
    
    private Route(String route) {
        this.route = route;
    }
    
    public String getRoute() {
        return route;
    }
}
