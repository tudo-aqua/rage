/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2023 The RAGe Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.aqua.rage.util

import java.util.concurrent.Callable

/** Create a list of [Callable]s calling [function] with each value in [values1]. */
fun <T1, V> createCallableVariants(values1: Iterable<T1>, function: (T1) -> V): List<Callable<V>> =
    buildList {
      values1.forEach { v1 -> this += Callable { function(v1) } }
    }

/**
 * Create a list of [Callable]s calling [function] with each value in [values1].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, V> createCallableVariants(
    values1: Collection<T1>,
    function: (T1) -> V
): List<Callable<V>> =
    buildList(values1.size) { values1.forEach { v1 -> this += Callable { function(v1) } } }

/** Create a list of [Callable]s calling [function] with each combination of values in [values2]. */
fun <T1, T2, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    function: (T1, T2) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 -> values2.forEach { v2 -> this += Callable { function(v1, v2) } } }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values2].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    function: (T1, T2) -> V
): List<Callable<V>> =
    buildList(values1.size * values2.size) {
      values1.forEach { v1 -> values2.forEach { v2 -> this += Callable { function(v1, v2) } } }
    }

/** Create a list of [Callable]s calling [function] with each combination of values in [values3]. */
fun <T1, T2, T3, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    function: (T1, T2, T3) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 -> values3.forEach { v3 -> this += Callable { function(v1, v2, v3) } } }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values3].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    function: (T1, T2, T3) -> V
): List<Callable<V>> =
    buildList(values1.size * values2.size * values3.size) {
      values1.forEach { v1 ->
        values2.forEach { v2 ->
          values3.forEach { v3 -> this += Callable { function(v1, v2, v3) } }
        }
      }
    }

/** Create a list of [Callable]s calling [function] with each combination of values in [values4]. */
fun <T1, T2, T3, T4, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    function: (T1, T2, T3, T4) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 -> this += Callable { function(v1, v2, v3, v4) } }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values4].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    function: (T1, T2, T3, T4) -> V
): List<Callable<V>> =
    buildList(values1.size * values2.size * values3.size * values4.size) {
      values1.forEach { v1 ->
        values2.forEach { v2 ->
          values3.forEach { v3 ->
            values4.forEach { v4 -> this += Callable { function(v1, v2, v3, v4) } }
          }
        }
      }
    }

/** Create a list of [Callable]s calling [function] with each combination of values in [values5]. */
fun <T1, T2, T3, T4, T5, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    function: (T1, T2, T3, T4, T5) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 -> this += Callable { function(v1, v2, v3, v4, v5) } }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values5].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    function: (T1, T2, T3, T4, T5) -> V
): List<Callable<V>> =
    buildList(values1.size * values2.size * values3.size * values4.size * values5.size) {
      values1.forEach { v1 ->
        values2.forEach { v2 ->
          values3.forEach { v3 ->
            values4.forEach { v4 ->
              values5.forEach { v5 -> this += Callable { function(v1, v2, v3, v4, v5) } }
            }
          }
        }
      }
    }

/** Create a list of [Callable]s calling [function] with each combination of values in [values6]. */
fun <T1, T2, T3, T4, T5, T6, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    function: (T1, T2, T3, T4, T5, T6) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 -> this += Callable { function(v1, v2, v3, v4, v5, v6) } }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values6].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    function: (T1, T2, T3, T4, T5, T6) -> V
): List<Callable<V>> =
    buildList(
        values1.size * values2.size * values3.size * values4.size * values5.size * values6.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 -> this += Callable { function(v1, v2, v3, v4, v5, v6) } }
                  }
                }
              }
            }
          }
        }

/** Create a list of [Callable]s calling [function] with each combination of values in [values7]. */
fun <T1, T2, T3, T4, T5, T6, T7, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    function: (T1, T2, T3, T4, T5, T6, T7) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 -> this += Callable { function(v1, v2, v3, v4, v5, v6, v7) } }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values7].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    function: (T1, T2, T3, T4, T5, T6, T7) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        this += Callable { function(v1, v2, v3, v4, v5, v6, v7) }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/** Create a list of [Callable]s calling [function] with each combination of values in [values8]. */
fun <T1, T2, T3, T4, T5, T6, T7, T8, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8) }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values8].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8) }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/** Create a list of [Callable]s calling [function] with each combination of values in [values9]. */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8, v9) }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values9].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8, v9) }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values10].
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10) }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values10].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10) }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values11].
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        this += Callable { function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11) }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values11].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                this += Callable {
                                  function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11)
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values12].
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          this += Callable {
                            function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values12].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  this += Callable {
                                    function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12)
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values13].
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            this += Callable {
                              function(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13)
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values13].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    this += Callable {
                                      function(
                                          v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13)
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values14].
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              this += Callable {
                                function(
                                    v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14)
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values14].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      this += Callable {
                                        function(
                                            v1,
                                            v2,
                                            v3,
                                            v4,
                                            v5,
                                            v6,
                                            v7,
                                            v8,
                                            v9,
                                            v10,
                                            v11,
                                            v12,
                                            v13,
                                            v14)
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values15].
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                this += Callable {
                                  function(
                                      v1,
                                      v2,
                                      v3,
                                      v4,
                                      v5,
                                      v6,
                                      v7,
                                      v8,
                                      v9,
                                      v10,
                                      v11,
                                      v12,
                                      v13,
                                      v14,
                                      v15)
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values15].
 *
 * This is an optimized version for values with length information.
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        this += Callable {
                                          function(
                                              v1,
                                              v2,
                                              v3,
                                              v4,
                                              v5,
                                              v6,
                                              v7,
                                              v8,
                                              v9,
                                              v10,
                                              v11,
                                              v12,
                                              v13,
                                              v14,
                                              v15)
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values16].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  this += Callable {
                                    function(
                                        v1,
                                        v2,
                                        v3,
                                        v4,
                                        v5,
                                        v6,
                                        v7,
                                        v8,
                                        v9,
                                        v10,
                                        v11,
                                        v12,
                                        v13,
                                        v14,
                                        v15,
                                        v16)
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values16].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          this += Callable {
                                            function(
                                                v1,
                                                v2,
                                                v3,
                                                v4,
                                                v5,
                                                v6,
                                                v7,
                                                v8,
                                                v9,
                                                v10,
                                                v11,
                                                v12,
                                                v13,
                                                v14,
                                                v15,
                                                v16)
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values17].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    values17: Iterable<T17>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  values17.forEach { v17 ->
                                    this += Callable {
                                      function(
                                          v1,
                                          v2,
                                          v3,
                                          v4,
                                          v5,
                                          v6,
                                          v7,
                                          v8,
                                          v9,
                                          v10,
                                          v11,
                                          v12,
                                          v13,
                                          v14,
                                          v15,
                                          v16,
                                          v17)
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values17].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    values17: Collection<T17>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size *
            values17.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          values17.forEach { v17 ->
                                            this += Callable {
                                              function(
                                                  v1,
                                                  v2,
                                                  v3,
                                                  v4,
                                                  v5,
                                                  v6,
                                                  v7,
                                                  v8,
                                                  v9,
                                                  v10,
                                                  v11,
                                                  v12,
                                                  v13,
                                                  v14,
                                                  v15,
                                                  v16,
                                                  v17)
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values18].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    values17: Iterable<T17>,
    values18: Iterable<T18>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  values17.forEach { v17 ->
                                    values18.forEach { v18 ->
                                      this += Callable {
                                        function(
                                            v1,
                                            v2,
                                            v3,
                                            v4,
                                            v5,
                                            v6,
                                            v7,
                                            v8,
                                            v9,
                                            v10,
                                            v11,
                                            v12,
                                            v13,
                                            v14,
                                            v15,
                                            v16,
                                            v17,
                                            v18)
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values18].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    values17: Collection<T17>,
    values18: Collection<T18>,
    function: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size *
            values17.size *
            values18.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          values17.forEach { v17 ->
                                            values18.forEach { v18 ->
                                              this += Callable {
                                                function(
                                                    v1,
                                                    v2,
                                                    v3,
                                                    v4,
                                                    v5,
                                                    v6,
                                                    v7,
                                                    v8,
                                                    v9,
                                                    v10,
                                                    v11,
                                                    v12,
                                                    v13,
                                                    v14,
                                                    v15,
                                                    v16,
                                                    v17,
                                                    v18)
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values19].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    values17: Iterable<T17>,
    values18: Iterable<T18>,
    values19: Iterable<T19>,
    function:
        (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  values17.forEach { v17 ->
                                    values18.forEach { v18 ->
                                      values19.forEach { v19 ->
                                        this += Callable {
                                          function(
                                              v1,
                                              v2,
                                              v3,
                                              v4,
                                              v5,
                                              v6,
                                              v7,
                                              v8,
                                              v9,
                                              v10,
                                              v11,
                                              v12,
                                              v13,
                                              v14,
                                              v15,
                                              v16,
                                              v17,
                                              v18,
                                              v19)
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values19].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    values17: Collection<T17>,
    values18: Collection<T18>,
    values19: Collection<T19>,
    function:
        (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size *
            values17.size *
            values18.size *
            values19.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          values17.forEach { v17 ->
                                            values18.forEach { v18 ->
                                              values19.forEach { v19 ->
                                                this += Callable {
                                                  function(
                                                      v1,
                                                      v2,
                                                      v3,
                                                      v4,
                                                      v5,
                                                      v6,
                                                      v7,
                                                      v8,
                                                      v9,
                                                      v10,
                                                      v11,
                                                      v12,
                                                      v13,
                                                      v14,
                                                      v15,
                                                      v16,
                                                      v17,
                                                      v18,
                                                      v19)
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values20].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    values17: Iterable<T17>,
    values18: Iterable<T18>,
    values19: Iterable<T19>,
    values20: Iterable<T20>,
    function:
        (
            T1,
            T2,
            T3,
            T4,
            T5,
            T6,
            T7,
            T8,
            T9,
            T10,
            T11,
            T12,
            T13,
            T14,
            T15,
            T16,
            T17,
            T18,
            T19,
            T20) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  values17.forEach { v17 ->
                                    values18.forEach { v18 ->
                                      values19.forEach { v19 ->
                                        values20.forEach { v20 ->
                                          this += Callable {
                                            function(
                                                v1,
                                                v2,
                                                v3,
                                                v4,
                                                v5,
                                                v6,
                                                v7,
                                                v8,
                                                v9,
                                                v10,
                                                v11,
                                                v12,
                                                v13,
                                                v14,
                                                v15,
                                                v16,
                                                v17,
                                                v18,
                                                v19,
                                                v20)
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values20].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    values17: Collection<T17>,
    values18: Collection<T18>,
    values19: Collection<T19>,
    values20: Collection<T20>,
    function:
        (
            T1,
            T2,
            T3,
            T4,
            T5,
            T6,
            T7,
            T8,
            T9,
            T10,
            T11,
            T12,
            T13,
            T14,
            T15,
            T16,
            T17,
            T18,
            T19,
            T20) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size *
            values17.size *
            values18.size *
            values19.size *
            values20.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          values17.forEach { v17 ->
                                            values18.forEach { v18 ->
                                              values19.forEach { v19 ->
                                                values20.forEach { v20 ->
                                                  this += Callable {
                                                    function(
                                                        v1,
                                                        v2,
                                                        v3,
                                                        v4,
                                                        v5,
                                                        v6,
                                                        v7,
                                                        v8,
                                                        v9,
                                                        v10,
                                                        v11,
                                                        v12,
                                                        v13,
                                                        v14,
                                                        v15,
                                                        v16,
                                                        v17,
                                                        v18,
                                                        v19,
                                                        v20)
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values21].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    values17: Iterable<T17>,
    values18: Iterable<T18>,
    values19: Iterable<T19>,
    values20: Iterable<T20>,
    values21: Iterable<T21>,
    function:
        (
            T1,
            T2,
            T3,
            T4,
            T5,
            T6,
            T7,
            T8,
            T9,
            T10,
            T11,
            T12,
            T13,
            T14,
            T15,
            T16,
            T17,
            T18,
            T19,
            T20,
            T21) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  values17.forEach { v17 ->
                                    values18.forEach { v18 ->
                                      values19.forEach { v19 ->
                                        values20.forEach { v20 ->
                                          values21.forEach { v21 ->
                                            this += Callable {
                                              function(
                                                  v1,
                                                  v2,
                                                  v3,
                                                  v4,
                                                  v5,
                                                  v6,
                                                  v7,
                                                  v8,
                                                  v9,
                                                  v10,
                                                  v11,
                                                  v12,
                                                  v13,
                                                  v14,
                                                  v15,
                                                  v16,
                                                  v17,
                                                  v18,
                                                  v19,
                                                  v20,
                                                  v21)
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values21].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    values17: Collection<T17>,
    values18: Collection<T18>,
    values19: Collection<T19>,
    values20: Collection<T20>,
    values21: Collection<T21>,
    function:
        (
            T1,
            T2,
            T3,
            T4,
            T5,
            T6,
            T7,
            T8,
            T9,
            T10,
            T11,
            T12,
            T13,
            T14,
            T15,
            T16,
            T17,
            T18,
            T19,
            T20,
            T21) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size *
            values17.size *
            values18.size *
            values19.size *
            values20.size *
            values21.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          values17.forEach { v17 ->
                                            values18.forEach { v18 ->
                                              values19.forEach { v19 ->
                                                values20.forEach { v20 ->
                                                  values21.forEach { v21 ->
                                                    this += Callable {
                                                      function(
                                                          v1,
                                                          v2,
                                                          v3,
                                                          v4,
                                                          v5,
                                                          v6,
                                                          v7,
                                                          v8,
                                                          v9,
                                                          v10,
                                                          v11,
                                                          v12,
                                                          v13,
                                                          v14,
                                                          v15,
                                                          v16,
                                                          v17,
                                                          v18,
                                                          v19,
                                                          v20,
                                                          v21)
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values22].
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    T22,
    V> createCallableVariants(
    values1: Iterable<T1>,
    values2: Iterable<T2>,
    values3: Iterable<T3>,
    values4: Iterable<T4>,
    values5: Iterable<T5>,
    values6: Iterable<T6>,
    values7: Iterable<T7>,
    values8: Iterable<T8>,
    values9: Iterable<T9>,
    values10: Iterable<T10>,
    values11: Iterable<T11>,
    values12: Iterable<T12>,
    values13: Iterable<T13>,
    values14: Iterable<T14>,
    values15: Iterable<T15>,
    values16: Iterable<T16>,
    values17: Iterable<T17>,
    values18: Iterable<T18>,
    values19: Iterable<T19>,
    values20: Iterable<T20>,
    values21: Iterable<T21>,
    values22: Iterable<T22>,
    function:
        (
            T1,
            T2,
            T3,
            T4,
            T5,
            T6,
            T7,
            T8,
            T9,
            T10,
            T11,
            T12,
            T13,
            T14,
            T15,
            T16,
            T17,
            T18,
            T19,
            T20,
            T21,
            T22) -> V
): List<Callable<V>> = buildList {
  values1.forEach { v1 ->
    values2.forEach { v2 ->
      values3.forEach { v3 ->
        values4.forEach { v4 ->
          values5.forEach { v5 ->
            values6.forEach { v6 ->
              values7.forEach { v7 ->
                values8.forEach { v8 ->
                  values9.forEach { v9 ->
                    values10.forEach { v10 ->
                      values11.forEach { v11 ->
                        values12.forEach { v12 ->
                          values13.forEach { v13 ->
                            values14.forEach { v14 ->
                              values15.forEach { v15 ->
                                values16.forEach { v16 ->
                                  values17.forEach { v17 ->
                                    values18.forEach { v18 ->
                                      values19.forEach { v19 ->
                                        values20.forEach { v20 ->
                                          values21.forEach { v21 ->
                                            values22.forEach { v22 ->
                                              this += Callable {
                                                function(
                                                    v1,
                                                    v2,
                                                    v3,
                                                    v4,
                                                    v5,
                                                    v6,
                                                    v7,
                                                    v8,
                                                    v9,
                                                    v10,
                                                    v11,
                                                    v12,
                                                    v13,
                                                    v14,
                                                    v15,
                                                    v16,
                                                    v17,
                                                    v18,
                                                    v19,
                                                    v20,
                                                    v21,
                                                    v22)
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Create a list of [Callable]s calling [function] with each combination of values in [values22].
 *
 * This is an optimized version for values with length information.
 */
fun <
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    T22,
    V> createCallableVariants(
    values1: Collection<T1>,
    values2: Collection<T2>,
    values3: Collection<T3>,
    values4: Collection<T4>,
    values5: Collection<T5>,
    values6: Collection<T6>,
    values7: Collection<T7>,
    values8: Collection<T8>,
    values9: Collection<T9>,
    values10: Collection<T10>,
    values11: Collection<T11>,
    values12: Collection<T12>,
    values13: Collection<T13>,
    values14: Collection<T14>,
    values15: Collection<T15>,
    values16: Collection<T16>,
    values17: Collection<T17>,
    values18: Collection<T18>,
    values19: Collection<T19>,
    values20: Collection<T20>,
    values21: Collection<T21>,
    values22: Collection<T22>,
    function:
        (
            T1,
            T2,
            T3,
            T4,
            T5,
            T6,
            T7,
            T8,
            T9,
            T10,
            T11,
            T12,
            T13,
            T14,
            T15,
            T16,
            T17,
            T18,
            T19,
            T20,
            T21,
            T22) -> V
): List<Callable<V>> =
    buildList(
        values1.size *
            values2.size *
            values3.size *
            values4.size *
            values5.size *
            values6.size *
            values7.size *
            values8.size *
            values9.size *
            values10.size *
            values11.size *
            values12.size *
            values13.size *
            values14.size *
            values15.size *
            values16.size *
            values17.size *
            values18.size *
            values19.size *
            values20.size *
            values21.size *
            values22.size) {
          values1.forEach { v1 ->
            values2.forEach { v2 ->
              values3.forEach { v3 ->
                values4.forEach { v4 ->
                  values5.forEach { v5 ->
                    values6.forEach { v6 ->
                      values7.forEach { v7 ->
                        values8.forEach { v8 ->
                          values9.forEach { v9 ->
                            values10.forEach { v10 ->
                              values11.forEach { v11 ->
                                values12.forEach { v12 ->
                                  values13.forEach { v13 ->
                                    values14.forEach { v14 ->
                                      values15.forEach { v15 ->
                                        values16.forEach { v16 ->
                                          values17.forEach { v17 ->
                                            values18.forEach { v18 ->
                                              values19.forEach { v19 ->
                                                values20.forEach { v20 ->
                                                  values21.forEach { v21 ->
                                                    values22.forEach { v22 ->
                                                      this += Callable {
                                                        function(
                                                            v1,
                                                            v2,
                                                            v3,
                                                            v4,
                                                            v5,
                                                            v6,
                                                            v7,
                                                            v8,
                                                            v9,
                                                            v10,
                                                            v11,
                                                            v12,
                                                            v13,
                                                            v14,
                                                            v15,
                                                            v16,
                                                            v17,
                                                            v18,
                                                            v19,
                                                            v20,
                                                            v21,
                                                            v22)
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

/** Generator for the function variants. */
internal fun main() {
  for (i in 1..22) {
    println(
        """
        /**
         * Create a list of [Callable]s calling [function] with each ${if (i > 1) "combination of values" else "value"} in ${(i..i).joinToString(", ") { "[values$it]" }}.
         */
        fun <${(1..i).joinToString(", ") { "T$it" }}, V> createCallableVariants(
            ${(1..i).joinToString(", ") { "values$it: Iterable<T$it>" }},
            function: (${(1..i).joinToString(", ") { "T$it" }}) -> V): List<Callable<V>> =
            buildList {
                ${(1..i).joinToString("\n") { "values$it.forEach { v$it ->" }}
                    this += Callable { function(${(1..i).joinToString(", ") { "v$it" }}) }
                ${"}".repeat(i)}
            }
        
        /**
         * Create a list of [Callable]s calling [function] with each ${if (i > 1) "combination of values" else "value"} in ${(i..i).joinToString(", ") { "[values$it]" }}.
         *
         * This is an optimized version for values with length information.
         */
        fun <${(1..i).joinToString(", ") { "T$it" }}, V> createCallableVariants(
            ${(1..i).joinToString(", ") { "values$it: Collection<T$it>" }},
            function: (${(1..i).joinToString(", ") { "T$it" }}) -> V): List<Callable<V>> =
            buildList(${(1..i).joinToString(" * ") { "values$it.size" }}) {
                ${(1..i).joinToString("\n") { "values$it.forEach { v$it ->" }}
                    this += Callable { function(${(1..i).joinToString(", ") { "v$it" }}) }
                ${"}".repeat(i)}
            }
        """)
  }
}
