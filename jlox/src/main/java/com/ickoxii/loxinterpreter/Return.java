package com.ickoxii.loxinterpreter;

class Return extends RuntimeException {
  final Object value;

  Return(Object value) {
    /**
     * Disable some of JVM bullshitter since we are using
     * this for control flow and not error handling.
     * */
    super(null, null, false, false);
    this.value = value;
  }
}
