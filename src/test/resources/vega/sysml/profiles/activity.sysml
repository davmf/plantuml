package TestActivity3 {
  doc /* Order fulfillment with full feature set:
     *   - ExpansionRegion processing each line item of an order
     *     in parallel
     *   - InterruptibleRegion wrapping the slow shipping action so
     *     a cancellation can abort it via an interruptFlow edge
     *   - mix of controlFlow, objectFlow and interruptFlow edges.
     */

  private import ActivityProfile::*;

  part activity : Activity {
    :>> name = "Order Fulfillment - Full";

    part :>> rootActions {
      part receiveOrder  : Action { :>> name = "receiveOrder()";  }
      part pickItem      : Action { :>> name = "pickItem()";      }
      part packShipment  : Action { :>> name = "packShipment()";  }
      part shipOrder     : Action { :>> name = "shipOrder()";     }
      part cancelOrder   : Action { :>> name = "cancelOrder()";   }
      part refundCustomer: Action { :>> name = "refundCustomer()";}
    }

    part :>> objectNodes {
      part lineItems : ObjectNode {
        :>> name     = "LineItems";
        :>> typeName = "LineItem";
        :>> ordering = "unordered";
        :>> upper    = -1;
      }

      part pickedItems : ObjectNode {
        :>> name     = "PickedItems";
        :>> typeName = "LineItem";
        :>> ordering = "unordered";
        :>> upper    = -1;
      }
    }

    part :>> expansionRegions {
      part pickAll : ExpansionRegion {
        :>> name = "PickAllLineItems";
        :>> mode = ExpansionMode::parallelMode;

        part :>> inputCollections {
          part inputColl : ObjectNode {
            :>> name = "ItemsIn"; :>> typeName = "LineItem";
          }
        }
        part :>> outputCollections {
          part outputColl : ObjectNode {
            :>> name = "ItemsOut"; :>> typeName = "LineItem";
          }
        }
        part :>> inputElements {
          part eachItem : ObjectNode {
            :>> name = "EachItem"; :>> typeName = "LineItem";
          }
        }
        part :>> outputElements {
          part pickedItem : ObjectNode {
            :>> name = "PickedItem"; :>> typeName = "LineItem";
          }
        }

        :>> contents = (activity.rootActions.pickItem);
      }
    }

    part :>> interruptibleRegions {
      part shippingRegion : InterruptibleRegion {
        :>> name = "ShippingRegion";
        :>> actionContents = (
          activity.rootActions.packShipment,
          activity.rootActions.shipOrder
        );
      }
    }

    // Receive -> expansion region (object flow of the line items)
    connection receiveToPickAll : ActivityEdge {
      :>> kind = FlowKind::objectFlow;
      connect source to rootActions.receiveOrder.outgoing;
      connect target to expansionRegions.pickAll.incoming;
    }

    // Expansion output -> pack
    connection pickAllToPack : ActivityEdge {
      :>> kind = FlowKind::objectFlow;
      connect source to expansionRegions.pickAll.outgoing;
      connect target to rootActions.packShipment.incoming;
    }

    // Pack -> ship (control flow, inside the interruptible region)
    connection packToShip : ActivityEdge {
      :>> kind = FlowKind::controlFlow;
      connect source to rootActions.packShipment.outgoing;
      connect target to rootActions.shipOrder.incoming;
    }

    // Cancellation interrupting the shipping region
    connection cancelInterrupt : ActivityEdge {
      :>> name = "cancel -> interrupt shipping";
      :>> kind = FlowKind::interruptFlow;
      connect source to interruptibleRegions.shippingRegion.interruptOut;
      connect target to rootActions.cancelOrder.incoming;
    }

    // Cancel -> refund (normal control flow after the interrupt)
    connection cancelToRefund : ActivityEdge {
      :>> kind = FlowKind::controlFlow;
      connect source to rootActions.cancelOrder.outgoing;
      connect target to rootActions.refundCustomer.incoming;
    }
  }
}
