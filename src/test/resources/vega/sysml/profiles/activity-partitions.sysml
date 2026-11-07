package TestActivity2 {
  doc /* Order-fulfillment activity split across two Partitions (Sales
     * and Warehouse), with an ObjectNode buffering the Order between
     * them. Demonstrates partitions and objectFlow edges.
     */

  private import ActivityProfile::*;

  part activity : Activity {
    :>> name = "Order Fulfillment - Partitioned";

    part :>> rootActions {
      part takeOrder    : Action { :>> name = "takeOrder()";    }
      part validateCard : Action { :>> name = "validateCard()"; }
      part pickItems    : Action { :>> name = "pickItems()";    }
      part shipOrder    : Action { :>> name = "shipOrder()";    }
    }

    part :>> objectNodes {
      part orderQueue : ObjectNode {
        :>> name     = "OrderQueue";
        :>> typeName = "Order";
        :>> ordering = "FIFO";
        :>> upper    = 100;
      }
    }

    part :>> partitions {
      part sales : Partition {
        :>> name       = "Sales";
        :>> represents = "SalesDepartment";
        :>> actionContents = (
          activity.rootActions.takeOrder,
          activity.rootActions.validateCard
        );
        :>> objectContents = (activity.objectNodes.orderQueue);
      }

      part warehouse : Partition {
        :>> name       = "Warehouse";
        :>> represents = "WarehouseDepartment";
        :>> actionContents = (
          activity.rootActions.pickItems,
          activity.rootActions.shipOrder
        );
      }
    }

    // Sales-internal control flow
    connection takeToValidate : ActivityEdge {
      :>> kind = FlowKind::controlFlow;
      connect source to rootActions.takeOrder.outgoing;
      connect target to rootActions.validateCard.incoming;
    }

    // Sales -> queue (object flow handing off the Order)
    connection validateToQueue : ActivityEdge {
      :>> kind = FlowKind::objectFlow;
      connect source to rootActions.validateCard.outgoing;
      connect target to objectNodes.orderQueue.incoming;
    }

    // Queue -> warehouse
    connection queueToPick : ActivityEdge {
      :>> kind = FlowKind::objectFlow;
      connect source to objectNodes.orderQueue.outgoing;
      connect target to rootActions.pickItems.incoming;
    }

    // Warehouse-internal control flow
    connection pickToShip : ActivityEdge {
      :>> kind = FlowKind::controlFlow;
      connect source to rootActions.pickItems.outgoing;
      connect target to rootActions.shipOrder.incoming;
    }
  }
}
