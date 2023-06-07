/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.trabalhofinal.nodes.timers;

import projects.trabalhofinal.nodes.messages.WsnMsg;
import sinalgo.nodes.Node;
import sinalgo.nodes.timers.Timer;

/**
 *
 * @author gabrielsarubo
 */
public class WsnMessageTimer extends Timer {

    private WsnMsg message = null;

    public WsnMessageTimer(WsnMsg message) {
        this.message = message;
    }

    @Override
    public void fire() {
        ((Node)node).broadcast(message);
    }

}
