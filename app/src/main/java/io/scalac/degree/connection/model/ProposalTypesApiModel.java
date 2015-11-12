package io.scalac.degree.connection.model;

import java.io.Serializable;
import java.util.List;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 26/10/2015
 */
public class ProposalTypesApiModel implements Serializable {
    public String content;
    public List<ProposalSingleTypeApiModel> proposalTypes;
}
