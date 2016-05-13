/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.LimitedDepthFirst;
import com.sun.corba.se.spi.activation.ActivatorOperations;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import java.util.ArrayList;
import ontology.Types;
import tools.ElapsedCpuTimer;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import tools.Vector2d;
import controllers.Heuristics.SimpleStateHeuristic;
import java.util.HashMap;
import java.lang.Math;
/**
 *
 * @author binism
 */
public class Agent extends AbstractPlayer {
    private boolean DEBUG = true;
    /**
     * To avoid a circle,recording every return action 
     * using a hash table will speed up checking
    */
    private final int depth = 7;
    private static Queue<StateObservation> VisitedStateList = new LinkedList();
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        
    }
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapseTimer){
        //int depth = 100;
        printObject(stateObs);
        Types.ACTIONS bestAction = null;
        if(depth < 1){
            if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() return null");
            return bestAction;
        }
        ArrayList[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList[] movingPositions = stateObs.getMovablePositions();
        //Vector2d goalpos = fixedPositions[1].; //目标的坐标
        //Vector2d keypos = movingPositions[0].get(0).position; //钥匙的坐标
        StateObservation stCopy = stateObs.copy();
        ArrayList<Types.ACTIONS> actions = stCopy.getAvailableActions();
        if(actions.isEmpty()){
            if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() return null" );
            return bestAction;
        }
        if(actions.size() == 1){
            StateObservation tmp = stCopy.copy();
            stCopy.advance(actions.get(0));
            if(IsLegalAction(stCopy.copy(), VisitedStateList)&& !tmp.equalPosition(stCopy.copy())){
                bestAction = actions.get(0);
                VisitedStateList.offer(stCopy.copy());
                if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() return " + bestAction);
                return bestAction;
            }
            if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() return null");
            return null;
        }
        double MaxScore = Double.NEGATIVE_INFINITY;
        Stack<StateObservation> StateStack = new Stack();
        Queue<StateObservation> VisitedChildStateList = new LinkedList();
        for(int k = 0; k < actions.size(); k++){
            Types.ACTIONS action = actions.get(k);
            boolean LoopTag = true;
            stCopy = stateObs.copy();
            StateObservation tmp = stCopy.copy();
            stCopy.advance(action);
            if(DEBUG == true) System.out.println("action: " + action);
            if(IsLegalAction(stCopy.copy(), VisitedStateList)&&!tmp.equalPosition(stCopy.copy())){
                if(DEBUG==true) System.out.println("LegalAction:------------" + action + " action:");
                VisitedChildStateList.clear();
                StateObservation VisitedState = stCopy.copy();
                StateStack.clear();
                StateStack.push(stCopy.copy());
                VisitedChildStateList.offer(stCopy.copy());
                if(evaluateState(VisitedState) > MaxScore){
                    MaxScore = evaluateState(VisitedState);
                    bestAction = action;
                     if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() MaxScore=========== " + MaxScore);
                }
                if(StateStack.size() >= depth){
                    if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() LoopTag1 " + LoopTag);
                    LoopTag = false;
                }
                while(!StateStack.isEmpty() && LoopTag){
                    if(DEBUG) System.out.println("================in While==========");
                    stCopy = StateStack.peek().copy();
                    //double OldScore = stCopy.getGameScore();
                    ArrayList<Types.ACTIONS> child_actions = stCopy.getAvailableActions();
                    boolean HaveLegalMovement = false;
                    //boolean FindKey = false;
                    if(!child_actions.isEmpty()&& LoopTag){
                        for(int k1 = 0; k1 < child_actions.size(); k1++){
                            Types.ACTIONS child_action = child_actions.get(k1);
                            stCopy = StateStack.peek().copy();
                            StateObservation child_tmp = stCopy.copy();
                            stCopy.advance(child_action);
                            if(DEBUG == true) System.out.println("action = " + child_action);
                            if(IsLegalAction(stCopy.copy(),VisitedStateList)&&!child_tmp.equalPosition(stCopy.copy())&&IsLegalChildAction(stCopy.copy(), VisitedChildStateList)){
                                HaveLegalMovement = true;
                                VisitedState = stCopy.copy();
                                if(DEBUG==true) System.out.println("Child LegalAction:------------" + child_action + " action:");
                                StateStack.push(stCopy.copy());
                                if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() StateStack.size " + StateStack.size());
                                if(evaluateState(VisitedState) > MaxScore){
                                    MaxScore = evaluateState(VisitedState);
                                    bestAction = action;
                                    if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() MaxScore============= " + MaxScore);
                                    //if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() LoopTag2 " + LoopTag);
                                }
                                if(StateStack.size() >= depth){
                                    if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() LoopTag1 " + LoopTag);
                                    LoopTag = false;
                                }
                                break;
                            }
                        }
                    }
                    if(HaveLegalMovement == false){
                        //if(DEBUG == true) System.out.println("Action remove! ActionList.size:" + ActionList.size());
                        StateStack.pop();
                    }
                }
                if(evaluateState(VisitedState) > MaxScore){
                    MaxScore = evaluateState(VisitedState);
                    bestAction = action;
                    if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() MaxScore================ " + MaxScore);
                    //if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() LoopTag2 " + LoopTag);
                }
            }
        }
        stCopy =   stateObs.copy();
        if(DEBUG) System.out.println("controllers.LimitedDepthFirst.Agent.act() return " + bestAction);
        if(DEBUG) System.out.println("MaxScore: " + MaxScore);
        stCopy.advance(bestAction);
        VisitedStateList.offer(stCopy.copy());
        return bestAction;
    }
    
    private boolean IsLegalAction(StateObservation sta, Queue<StateObservation> VisitedStateList){
        //Stack<StateObservation> tmpStack = (Stack<StateObservation>) actionsStack.clone();
        if(VisitedStateList.isEmpty()){
            //VisitedStateList.offer(sta.copy());
            if(DEBUG == true) System.out.println("IsLegalAction true 1");
            return true;
        }
        for(StateObservation state : VisitedStateList){
           if(sta.equalPosition(state.copy())){
               if(DEBUG == true) System.out.println("IsLegalAction false 1");
               return false;
           }
        }
        if(sta.isGameOver() && sta.getGameWinner()!= Types.WINNER.PLAYER_WINS){
             if(DEBUG == true) System.out.println("IsLegalAction false 2");
            return false;
        }
        //VisitedStateList.offer(sta.copy());
        if(DEBUG == true) System.out.println("IsLegalAction true 2");
        return true;
    }
    private boolean IsLegalChildAction(StateObservation child_sta, Queue<StateObservation> VisitedChildStateList){
        //Stack<StateObservation> tmpStack = (Stack<StateObservation>) actionsStack.clone();
        if(VisitedChildStateList.isEmpty()){
            //VisitedStateList.offer(sta.copy());
            if(DEBUG == true) System.out.println("IsChildLegalAction true 1");
            return true;
        }
        for(StateObservation state : VisitedChildStateList){
           if(child_sta.equalPosition(state.copy())){
               if(DEBUG == true) System.out.println("IsChildLegalAction false 1");
               return false;
           }
        }
        if(child_sta.isGameOver() && child_sta.getGameWinner()!= Types.WINNER.PLAYER_WINS){
             if(DEBUG == true) System.out.println("IsChildLegalAction false 2");
            return false;
        }
        VisitedChildStateList.offer(child_sta.copy());
        if(DEBUG == true) System.out.println("IsChildLegalAction true 2");
        return true;
    }
    
    private double evaluateState(StateObservation stateObs) {
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        //Vector2d goalpos = fixedPositions[1].get(0).position; //目标的坐标
        //Vector2d keypos = movingPositions[0].get(0).position; //钥匙的坐标
        Vector2d avatarpos = stateObs.getAvatarPosition(); //精灵的坐标
        int avatorType = stateObs.getAvatarType();
        double cost = 0.0;
        if(stateObs.isGameOver()&&stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS)
            return Double.POSITIVE_INFINITY;
        if(avatorType != 4){//没找到钥匙
            Vector2d keypos = movingPositions[0].get(0).position; //钥匙的坐标
            cost = (Math.abs(avatarpos.x - keypos.x) + Math.abs(avatarpos.y - keypos.y))/50;
        }
        else {
            Vector2d goalpos = fixedPositions[1].get(0).position; //目标的坐标
            cost = (Math.abs(avatarpos.x - goalpos.x) + Math.abs(avatarpos.y - goalpos.y))/50;
        }
        return  10 * stateObs.getGameScore() - cost;   
    }
     private void printObject(StateObservation stateObs){
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        System.out.println("ImmovablePositionsList");
        for(int ik = 0; ik < fixedPositions.length; ik++){
            System.out.println("fixedPositions[" + ik +"]" + fixedPositions[ik].size() + "++++++++++++++++");
            for(int iik = 0; iik < fixedPositions[ik].size();iik++)
                System.out.println("itype: " + fixedPositions[ik].get(iik).itype
                       + "\nstring: " + fixedPositions[ik].get(iik).toString()
                       + "\ncategory: " + fixedPositions[ik].get(iik).category
                       + "\nobsID " + fixedPositions[ik].get(iik).obsID
                       + "\nposition " + fixedPositions[ik].get(iik).position
                       + "\nreference " + fixedPositions[ik].get(iik).reference
                       + "\nsqDist " + fixedPositions[ik].get(iik).sqDist
                );
        }
        System.out.println("movablePositionsList");
        for(int ik = 0; ik < movingPositions.length; ik++){
            System.out.println("movablePositions[" + ik +"]" + movingPositions[ik].size() + "++++++++++++++++");
            for(int iik = 0; iik < movingPositions[ik].size();iik++)
                System.out.println("itype: " + movingPositions[ik].get(iik).itype
                       + "\nstring: " + movingPositions[ik].get(iik).toString()
                       + "\ncategory: " + movingPositions[ik].get(iik).category
                       + "\nobsID " + movingPositions[ik].get(iik).obsID
                       + "\nposition " + movingPositions[ik].get(iik).position
                       + "\nreference " + movingPositions[ik].get(iik).reference
                       + "\nsqDist " + movingPositions[ik].get(iik).sqDist
                );
        }
        System.out.println("avator+++++++");
        System.out.println("avator position: " + stateObs.getAvatarPosition() 
                + "avator type: " + stateObs.getAvatarType()
        );
    }
}
