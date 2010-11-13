package su.msu.cs.lvk.accorute.http.model;

import su.msu.cs.lvk.accorute.decisions.ParameterValueDecision;
import su.msu.cs.lvk.accorute.decisions.RequestComposerDecomposer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: george
 * Date: 12.04.2010
 * Time: 12:19:25
 * To change this template use File | Settings | File Templates.
 */
public class Action {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name + '\'' +
                ", actionParameters=" + actionParameters +
                ", actionID=" + actionID +
                '}';
    }

    public EntityID getActionID() {
        return actionID;
    }

    public void setActionID(EntityID actionID) {
        this.actionID = actionID;
    }

    public List<ActionParameter> getActionParameters() {
        return actionParameters;
    }

    public void setActionParameters(List<ActionParameter> actionParameters) {
        this.actionParameters = actionParameters;
    }

    private String name;
    private List<ActionParameter> actionParameters;
    private EntityID actionID = EntityID.NOT_INITIALIZED;

    public Action(String name,List<ActionParameter> params ){
        this.name = name;
        this.actionParameters = params;
    }

    boolean equals(Action other){
        return actionID == other.getActionID();
    }
}
