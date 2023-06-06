/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.sample7.nodes.timers;

import projects.sample7.nodes.messages.Packet;
import projects.sample7.nodes.nodeImplementations.MyNode;
import sinalgo.nodes.timers.Timer;

/**
 *
 * @author pozza
 */
public class TimerScheduler extends Timer {

    private Packet message = null;

    public TimerScheduler(Packet message) {
        this.message = message;
    }

    @Override
    public void fire() {
        ((MyNode)node).broadcast(message);
    }

}
