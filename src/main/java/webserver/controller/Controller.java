/**
 * 
 */
package webserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.customaction.annotation.RequestMapping;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

/**
 * File 요청이 아닌 요청에 대한 액션을 정의
 *
 * @author HG
 * @since 2018. 7. 30.
 * @see
 *
 */
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    
    @RequestMapping(value = "/", method = "GET")
    public String index(HttpRequest request, HttpResponse response) {
        return "index.html";
    }
    
    @RequestMapping(value = "/user/create", method = "POST")
    public String regist(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"), request.getParameter("password"),
                request.getParameter("name"), request.getParameter("email"));
        
        DataBase.addUser(user);
        
        response.setResponseStatus(302);
        
        return "redirect:/";
    }
    
    @RequestMapping(value = "/users", method = "GET")
    public String viewAll(HttpRequest request, HttpResponse response) {
        logger.info("{}", DataBase.findAll());
        
        return "";
    }
    
}
