package SettingMode.trait_link

trait Two extends One {
  override def lr(): Unit = {
    print("\nTwo");
    super.lr();
  }
}
