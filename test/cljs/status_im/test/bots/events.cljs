(ns status-im.test.bots.events
  (:require [cljs.test :refer-macros [deftest is testing]]
            [status-im.bots.events :as bots-events]))

(def ^:private initial-db
  {:bot-db {}
   :bot-subscriptions {}
   :contacts/contacts
   {"bot1" {:subscriptions
            {:feeExplanation
             {:subscriptions {:fee ["sliderValue"]
                              :tx ["transaction"]}}}}
    "bot2" {:subscriptions
            {:roundedValue
             {:subscriptions {:amount ["input"]}}}}
    "bot3" {:subscriptions
            {:warning
             {:subscriptions {:amount ["input"]}}}}}})

(deftest add-active-bot-subscriptions-test
  (testing "That active bot subscriptions are correctly transformed and added to db"
    (let [db (bots-events/add-active-bot-subscriptions initial-db #{"bot1" "bot3"})]
      (is (= #{"bot1" "bot3"} (-> db :bot-subscriptions keys set)))
      (is (= {[:sliderValue] {:feeExplanation {:fee [:sliderValue]
                                               :tx [:transaction]}}
              [:transaction] {:feeExplanation {:fee [:sliderValue]
                                               :tx [:transaction]}}})))))

;; TODO(janherich): test recalculation of subscriptions as well, once we remove
;; the hacky hardcoded way of looking command owner-id & get rid of the fixed
;; way how we keep bot-db (currently only for `current-chat-id`)
(deftest set-in-bot-db-test
  (testing "That setting in value in bot-db correctly updates bot-db"
    (let [event-fx (bots-events/set-in-bot-db initial-db
                                              {:bot "bot1"
                                               :path [:sliderValue]
                                               :value 2})]
      (is (= 2 (get-in event-fx [:db :bot-db "bot1" :sliderValue]))))))
