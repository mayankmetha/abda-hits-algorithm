
package com.pes.giraph;


import org.apache.giraph.aggregators.DoubleSumAggregator;
import org.apache.giraph.master.DefaultMasterCompute;

public class MasterComputer extends DefaultMasterCompute {
    public static String HubId = "HubScoreAggregator";
    public static String AuthId = "AuthScoreAggregator";
        
    @Override
    public void initialize()  throws InstantiationException,
                                     IllegalAccessException {
        System.out.println("************************ Entered Master *************");
        registerAggregator(HubId, DoubleSumAggregator.class);
        registerAggregator(AuthId, DoubleSumAggregator.class);
    }
}


