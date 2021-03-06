package org.nanocontainer.nanowar.sample.webwork2;

import com.opensymphony.xwork.ActionSupport;
import org.nanocontainer.nanowar.sample.model.Cheese;
import org.nanocontainer.nanowar.sample.service.CheeseService;

public class AddCheese extends ActionSupport {

    private Cheese cheese = new Cheese();
    private CheeseService cheeseService;

    public AddCheese(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }

    public Cheese getCheese() {
        return cheese;
    }


    public String execute() throws Exception {
        cheeseService.save(cheese);
        return SUCCESS;
    }

}
