package com.devoxx.connection.model;

import java.io.Serializable;
import java.util.List;

public class ProposalTypesApiModel implements Serializable {
    public String content;
    public List<ProposalSingleTypeApiModel> proposalTypes;
}
