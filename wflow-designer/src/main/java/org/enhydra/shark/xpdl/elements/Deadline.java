package org.enhydra.shark.xpdl.elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLElementChangeListener;
import org.enhydra.shark.xpdl.XPDLConstants;

public class Deadline extends XMLComplexElement implements XMLElementChangeListener {

    private DeadlineLimit refDeadlineLimit;
    private XMLAttribute attrDurationUnit;
    private DeadlineCondition refDeadlineCondition;

    public Deadline(Deadlines parent) {
        super(parent, true);
        parent.addListener(this);
    }

    protected void fillStructure() {
        //CUSTOM
        refDeadlineCondition = new DeadlineCondition(this); // min=1, max=1
        ExceptionName refExceptionName = new ExceptionName(this); // min=1, max=1
        refDeadlineLimit = new DeadlineLimit(this); //min=1, max=1
        XMLAttribute attrExecution = new XMLAttribute(this, "Execution",
                false, new String[]{
                    XPDLConstants.EXECUTION_NONE,
                    XPDLConstants.EXECUTION_ASYNCHR,
                    XPDLConstants.EXECUTION_SYNCHR
                }, 1);

        attrDurationUnit = new XMLAttribute(this, "DurationUnit",
                false, new String[]{
                    XPDLConstants.DURATION_UNIT_D,
                    XPDLConstants.DURATION_UNIT_h,
                    XPDLConstants.DURATION_UNIT_m,
                    XPDLConstants.DURATION_UNIT_s
                }, 0);

        add(attrExecution);
        add(refDeadlineCondition);
        add(refExceptionName);
        
        addCustomElements();
        //END CUSTOM
    }

    public XMLAttribute getExecutionAttribute() {
        return (XMLAttribute) get("Execution");
    }

    public String getExecution() {
        return getExecutionAttribute().toValue();
    }

    public void setExecutionNONE() {
        getExecutionAttribute().setValue(XPDLConstants.EXECUTION_NONE);
    }

    public void setExecutionASYNCHR() {
        getExecutionAttribute().setValue(XPDLConstants.EXECUTION_ASYNCHR);
    }

    public void setExecutionSYNCHR() {
        getExecutionAttribute().setValue(XPDLConstants.EXECUTION_SYNCHR);
    }

    public XMLAttribute getDurationUnitAttribute() {
        return (XMLAttribute) get("DurationUnit");
    }

    public String getDurationUnit() {
        return getDurationUnitAttribute().toValue();
    }

    public String getDeadlineLimit() {
        return get("DeadlineLimit").toValue();
    }

    public void setDeadlineLimit(String deadlineLimit) {
        set("DeadlineLimit", deadlineLimit);
    }

    public String getDeadlineCondition() {
        return get("DeadlineCondition").toValue();
    }

    public void setDeadlineCondition(String deadlineCondition) {
        set("DeadlineCondition", deadlineCondition);
    }

    public String getExceptionName() {
        return get("ExceptionName").toValue();
    }

    public void setExceptionName(String exceptionName) {
        set("ExceptionName", exceptionName);
    }

    //CUSTOM
    public void xmlElementChanged(XMLElementChangeInfo changedInfo) {

        Deadlines changedDeadlines = (Deadlines) changedInfo.getChangedElement();

        if (changedDeadlines != null && changedDeadlines.size() > 0) {
            List deadlineList = changedInfo.getChangedSubElements();
            Deadline changedDeadline = (Deadline) deadlineList.get(0);
            if (changedDeadline == this) {
                XMLAttribute durationUnit = (XMLAttribute) changedDeadline.get("DurationUnit");

                String changedDeadlineLimit = changedDeadline.getDeadlineLimit();
                if (changedDeadlineLimit != null && changedDeadlineLimit.trim().length() > 0) {
                    char durationUnitChar = durationUnit.toValue().charAt(0);
                    String variableCondition = "";
                    switch (durationUnitChar) {
                        case 'D':
                            variableCondition += (24 * 60 * 60 * 1000);
                            break;
                        case 'h':
                            variableCondition += (60 * 60 * 1000);
                            break;
                        case 'm':
                            variableCondition += (60 * 1000);
                            break;
                        case 's':
                            variableCondition += 1000;
                    }
                    variableCondition = "(" + changedDeadlineLimit + "*" + variableCondition + ")";

                    set("DeadlineCondition",
                            "var " + durationUnitChar + "=new java.util.Date(); " + durationUnitChar + ".setTime(ACTIVITY_ACTIVATED_TIME.getTime()+" + variableCondition + "); " + durationUnitChar + ";");
                }
            }

            hideCustomElements();
        }
    }
    
    public void addCustomElements() {
        add(attrDurationUnit);
        add(refDeadlineLimit);
    }
    
    public void hideCustomElements() {
        // set deadline limit from deadline condition
        String deadlineCondition = getDeadlineCondition();
        String deadlineLimit = "";
        Pattern pattern = Pattern.compile("\\+\\(.+\\*");
        Matcher matcher = pattern.matcher(deadlineCondition);
        if (matcher.find()) {
            String match = matcher.group();
            deadlineLimit = match.substring(2, match.length()-1);
        }
        refDeadlineLimit.setValue(deadlineLimit);
        
        // set duration unit from deadline condition
        String durationUnit = XPDLConstants.DURATION_UNIT_D;
        pattern = Pattern.compile("\\*\\d+\\)");
        matcher = pattern.matcher(deadlineCondition);
        if (matcher.find()) {
            String match = matcher.group();
            String millis = match.substring(1, match.length()-1);
            if ("1000".equals(millis)) {
                durationUnit = XPDLConstants.DURATION_UNIT_s;
            } else if ("60000".equals(millis)) {
                durationUnit = XPDLConstants.DURATION_UNIT_m;
            } else if ("3600000".equals(millis)) {
                durationUnit = XPDLConstants.DURATION_UNIT_h;
            } else {
                durationUnit = XPDLConstants.DURATION_UNIT_D;
            }
        }
        attrDurationUnit.setValue(durationUnit);

        // remove elements
        elements.remove(refDeadlineLimit);
        elements.remove(attrDurationUnit);
    }
    //END CUSTOM

}
