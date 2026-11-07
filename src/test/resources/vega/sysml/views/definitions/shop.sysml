package Shop {
  enum def Size { enum small; enum large; }
  abstract part def Item {
    attribute price : Real;
  }
  part def Cup :> Item {
    attribute size : Size = Size::small;
    port handle : Grip[0..1];
  }
  port def Grip;
}
