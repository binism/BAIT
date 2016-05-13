/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.depthFirst;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import java.util.ArrayList;
import ontology.Types;
import tools.ElapsedCpuTimer;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
/**
 *
 * @author binism
 */

public class Agent extends AbstractPlayer {
    private static int ActTimes = 0;
    private boolean DEBUG = true;
    private static ArrayList<Types.ACTIONS> ActionList = new ArrayList<Types.ACTIONS>();
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

    }
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        if(ActTimes == 0){
            StateObservation stCopy = stateObs.copy();
            Stack<StateObservation> StateStack = new Stack();
            Queue<StateObservation> VisitedStateList = new LinkedList();
            if(DEBUG == true){
                if(VisitedStateList.isEmpty()){
                    System.out.println("VisitedStateList.isEmpty 1");
                }
            }
            FindKeyDFS(stCopy,  StateStack,  VisitedStateList);
            stCopy = StateStack.peek().copy();
            StateStack.clear();
            VisitedStateList.clear();
            FindGoalDFS(stCopy, StateStack, VisitedStateList);
        }
        if(DEBUG == true) System.out.println("ActTimes:"+ ActTimes);
        ActTimes++;
        ActTimes = ActTimes >= ActionList.size() ? ActionList.size() : ActTimes;
        return ActionList.get(ActTimes - 1);
    }
    private boolean IsLegalAction(StateObservation sta, Queue<StateObservation> VisitedStateList){
        //Stack<StateObservation> tmpStack = (Stack<StateObservation>) actionsStack.clone();
        if(VisitedStateList.isEmpty()){
            VisitedStateList.offer(sta.copy());
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
        VisitedStateList.offer(sta.copy());
        if(DEBUG == true) System.out.println("IsLegalAction true 2");
        return true;
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
            System.out.println("movablePositionList[" + ik +"]" + movingPositions[ik].size() + "++++++++++++++++");
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
    private void FindKeyDFS(StateObservation stCopy, Stack<StateObservation> StateStack, Queue<StateObservation> VisitedStateList){
        StateStack.push(stCopy.copy());
        VisitedStateList.offer(stCopy.copy());
        while(!StateStack.isEmpty()){
            stCopy = StateStack.peek().copy();
            double OldScore = stCopy.getGameScore();
            ArrayList<Types.ACTIONS> actions = stCopy.getAvailableActions();
            boolean HaveLegalMovement = false;
            boolean FindKey = false;
            if(!actions.isEmpty()){
                for(int k = 0; k < actions.size(); k++){
                    Types.ACTIONS action = actions.get(k);
                    stCopy = StateStack.peek().copy();
                    StateObservation tmp = stCopy.copy();
                    stCopy.advance(action);
                    if(DEBUG == true) System.out.println("action = " + action);
                    if(IsLegalAction(stCopy.copy(),VisitedStateList)&&!tmp.equalPosition(stCopy.copy())){
                        if(DEBUG==true) System.out.println("LegalAction:------------" + action + " action:");
                        StateStack.push(stCopy.copy());
                        ActionList.add(action);
                        HaveLegalMovement = true;
                        if(stCopy.getGameScore() == OldScore + 5.0)
                            FindKey = true;
                        break;
                    }
                }
            }
            if(FindKey == true){
                if(DEBUG == true)System.out.println("FindKey!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                break;
            }
            if(HaveLegalMovement == false){
                if(DEBUG == true) System.out.println("Action remove! ActionList.size:" + ActionList.size());
                StateStack.pop();
                if(!ActionList.isEmpty())
                    ActionList.remove(ActionList.size() - 1);
            }
        }
    }
    private void FindGoalDFS(StateObservation stCopy, Stack<StateObservation> StateStack, Queue<StateObservation> VisitedStateList){
         StateStack.push(stCopy.copy());
            VisitedStateList.offer(stCopy.copy());
            while(!StateStack.isEmpty()){
                stCopy = StateStack.peek().copy();
                ArrayList<Types.ACTIONS> actions = stCopy.getAvailableActions();
                boolean HaveLegalMovement = false;
                if(!actions.isEmpty()){
                    for(int k = 0; k < actions.size(); k++){
                        Types.ACTIONS action = actions.get(k);
                        stCopy = StateStack.peek().copy();
                        StateObservation tmp = stCopy.copy();
                        stCopy.advance(action);
                        if(DEBUG == true) System.out.println("action = " + action);
                        if(IsLegalAction(stCopy.copy(),VisitedStateList)&&!tmp.equalPosition(stCopy)){
                            if(DEBUG == true) System.out.println("LegalAction:------------" + action + " action:");
                            StateStack.push(stCopy.copy());
                            ActionList.add(action);
                            HaveLegalMovement = true;
                            break;
                        }
                    }
                }
                if(stCopy.isGameOver()&& stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS){
                    if(DEBUG == true){
                        System.out.println("GameWin!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        printObject(stCopy.copy());
                        for(Types.ACTIONS tmpAction : ActionList){
                            System.out.println("Action:"+ tmpAction);
                        }
                    }
                    break;
                }
                if(HaveLegalMovement == false){
                    if(DEBUG == true) System.out.println("Action remove! ActionList.size:" + ActionList.size());
                    StateStack.pop();
                    if(!ActionList.isEmpty())
                        ActionList.remove(ActionList.size() - 1);
                }
            }
    }
}
