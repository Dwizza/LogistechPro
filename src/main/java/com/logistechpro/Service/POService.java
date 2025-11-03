package com.logistechpro.Service;

import com.logistechpro.DTO.Request.PORequest;
import com.logistechpro.DTO.Response.POResponse;

public interface POService {
        POResponse create(PORequest request);
        POResponse validatePurchaseOrder(Long poId);
}
