(ns eldar.endpoint.ui-test
  (:require [clojure.test :refer :all]
            [eldar.endpoint.ui :as ui]))

(def handler
  (ui/ui-endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
