package TestSwArch3 {
  doc /* Full SoftwareArchitectureProfile sample: five layers
     * (hardware, driver, controller, application, service) holding
     * thirteen components, joined by twelve SignalFlow edges and
     * fourteen Dependency edges. Mirrors the canonical model in
     * /home/dxf/sysmlv2/layered-architecture.sysml and exercises
     * bidirectional signal flows (modelled as two SignalFlows over
     * signalsBidir), multi-target dependency fan-out, and the full
     * five-layer rendering pipeline in sysml-sw-arch-to-drawio.
     */

  private import SoftwareArchitectureProfile::*;

  part def FullStackAssembly {

    // ---- Layers (flat siblings) with components nested inside ----

    part hardwareLayer : Layer {
      :>> kind = LayerKind::hardware;
      part inputDevice  : LayerComponent;
      part outputDevice : LayerComponent;
      part ioDevice     : LayerComponent;
    }

    part driverLayer : Layer {
      :>> kind = LayerKind::driver;
      part inputDriver  : LayerComponent;
      part outputDriver : LayerComponent;
      part ioDriver     : LayerComponent;
    }

    part controllerLayer : Layer {
      :>> kind = LayerKind::controller;
      part inputController  : LayerComponent;
      part outputController : LayerComponent;
      part ioController     : LayerComponent;
    }

    part applicationLayer : Layer {
      :>> kind = LayerKind::application;
      part app1 : LayerComponent;
      part app2 : LayerComponent;
    }

    part serviceLayer : Layer {
      :>> kind = LayerKind::service;
      part svc1 : LayerComponent;
      part svc2 : LayerComponent;
    }

    // ---- Signal flows (solid arrows) ------------------------------

    connection sf_in_dev_drv  : SignalFlow connect hardwareLayer.inputDevice.signalsOut          to driverLayer.inputDriver.signalsIn;
    connection sf_in_drv_ctl  : SignalFlow connect driverLayer.inputDriver.signalsOut            to controllerLayer.inputController.signalsIn;
    connection sf_in_ctl_app1 : SignalFlow connect controllerLayer.inputController.signalsOut    to applicationLayer.app1.signalsIn;
    connection sf_in_ctl_app2 : SignalFlow connect controllerLayer.inputController.signalsOut    to applicationLayer.app2.signalsIn;
    connection sf_app1_out    : SignalFlow connect applicationLayer.app1.signalsOut              to controllerLayer.outputController.signalsIn;
    connection sf_app2_out    : SignalFlow connect applicationLayer.app2.signalsOut              to controllerLayer.outputController.signalsIn;
    connection sf_out_ctl_drv : SignalFlow connect controllerLayer.outputController.signalsOut   to driverLayer.outputDriver.signalsIn;
    connection sf_out_drv_dev : SignalFlow connect driverLayer.outputDriver.signalsOut           to hardwareLayer.outputDevice.signalsIn;

    // IO chain: bidirectional, modelled as two SignalFlows per hop.
    connection sf_io_dev_drv_fwd : SignalFlow connect hardwareLayer.ioDevice.signalsBidir       to driverLayer.ioDriver.signalsBidir;
    connection sf_io_drv_dev_rev : SignalFlow connect driverLayer.ioDriver.signalsBidir         to hardwareLayer.ioDevice.signalsBidir;
    connection sf_io_drv_ctl_fwd : SignalFlow connect driverLayer.ioDriver.signalsBidir         to controllerLayer.ioController.signalsBidir;
    connection sf_io_ctl_drv_rev : SignalFlow connect controllerLayer.ioController.signalsBidir to driverLayer.ioDriver.signalsBidir;

    // ---- Dependencies (dashed arrows; dependent.depOut -> provider.depIn) ----

    connection dep_drv_dev       : Dependency connect driverLayer.inputDriver.depOut          to hardwareLayer.inputDevice.depIn;
    connection dep_ctl_drv       : Dependency connect controllerLayer.inputController.depOut  to driverLayer.inputDriver.depIn;
    connection dep_app1_inctl    : Dependency connect applicationLayer.app1.depOut            to controllerLayer.inputController.depIn;
    connection dep_app2_inctl    : Dependency connect applicationLayer.app2.depOut            to controllerLayer.inputController.depIn;
    connection dep_app1_outctl   : Dependency connect applicationLayer.app1.depOut            to controllerLayer.outputController.depIn;
    connection dep_app2_outctl   : Dependency connect applicationLayer.app2.depOut            to controllerLayer.outputController.depIn;
    connection dep_outdrv_outctl : Dependency connect driverLayer.outputDriver.depOut         to controllerLayer.outputController.depIn;
    connection dep_outdev_outdrv : Dependency connect hardwareLayer.outputDevice.depOut       to driverLayer.outputDriver.depIn;
    connection dep_app1_svc1     : Dependency connect applicationLayer.app1.depOut            to serviceLayer.svc1.depIn;
    connection dep_app2_svc2     : Dependency connect applicationLayer.app2.depOut            to serviceLayer.svc2.depIn;
    connection dep_inctl_svc1    : Dependency connect controllerLayer.inputController.depOut  to serviceLayer.svc1.depIn;
    connection dep_app1_ioctl    : Dependency connect applicationLayer.app1.depOut            to controllerLayer.ioController.depIn;
    connection dep_ioctl_iodrv   : Dependency connect controllerLayer.ioController.depOut     to driverLayer.ioDriver.depIn;
    connection dep_iodrv_iodev   : Dependency connect driverLayer.ioDriver.depOut             to hardwareLayer.ioDevice.depIn;
  }
}
