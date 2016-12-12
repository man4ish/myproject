/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cornell.qatarmed.planrnaseq;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 *
 * @author pak2013
 */
public class SimpleLoginUI extends UI {

    @Override
    protected void init(VaadinRequest request) {

        //
        // Create a new instance of the navigator. The navigator will attach
        // itself automatically to this view.
        //
        new Navigator(this, this);

        //
        // The initial log view where the user can login to the application
        //
    //    getNavigator().addView(SimpleLoginView.NAME, SimpleLoginView.class);//
             getNavigator().addView(AnnotateViewLogin.ANNOTATIONVIEW, AnnotateViewLogin.class);//
            //    getNavigator().addView(AnnotateView.ANNOTATIONVIEW, AnnotateView.class);//
        
        

        //
        // Add the main view of the application
        //
   //     getNavigator().addView(SimpleLoginMainView.NAME,
     //           SimpleLoginMainView.class);

        //
        // We use a view change handler to ensure the user is always redirected
        // to the login view if the user is not logged in.
        //
             /*
        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {

                // Check if a user has logged in
                System.out.println(" Checking user attribute in  Simple Login UI");
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof AnnotateViewLogin;

                if (!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet
                    // logged in
                     System.out.println(" Checking inside Simple Login UI");
                    getNavigator().navigateTo(AnnotateViewLogin.ANNOTATIONVIEW);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    // If someone tries to access to login view while logged in,
                    // then cancel
                    System.out.println("I am here logged in");
                    getNavigator().navigateTo(AnnotateViewLogin.ANNOTATIONVIEW);
                    return false;
                }else{
                    System.out.println("Either not logged in or view is not ok");
                    getNavigator().navigateTo(AnnotateViewLogin.ANNOTATIONVIEW);
                    return false;
                }

              //  return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

            }
        });
                     */
    }
}