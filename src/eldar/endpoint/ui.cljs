(ns eldar.endpoint.ui
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [register-handler path register-sub 
                                   dispatch dispatch-sync subscribe]]
            [ajax.core :refer [GET POST raw-response-format]]))

;; -- Subscription Handlers ---------------------------------------------------

(register-sub 
 :topologies
 (fn [db _]
   (reaction (:topologies @db))))

(register-sub
 :topologies/ids
 (fn [db _]
   (let [topologies (subscribe [:topologies])]
     (reaction (map #(keyword (:id %)) @topologies)))))

(register-sub
 :topologies/by-id
 (fn [db _]
   (let [topologies (subscribe [:topologies])]
     (reaction (into {} (map (fn [t] [(keyword (:id t)) t]) @topologies))))))

(register-sub
 :topology
 (fn [db [_ topo-id]]
   (let [id->topo (subscribe [:topologies/by-id])]
     (reaction (@id->topo topo-id)))))

(register-sub
 :editable-watchers
 (fn [db [_ topo-id]]
   (reaction (-> @db :editing :watchers topo-id))))

;; -- View Components ---------------------------------------------------------

(def watcher-attrs 
  [{:key :type
    :id-base "type-watcher_"
    :label "Type:"
    :placeholder "spouts or bolts"}
   {:key :check
    :id-base "check-watcher_"
    :label "Check:"
    :placeholder "any or all or bolt_id_1,bolt_id_2 etc"}
   {:key :metric
    :id-base "metric-watcher_"
    :label "Metric:"
    :placeholder "metric to watch from Storm API"}
   {:key :comparator
    :id-base "comp-watcher_"
    :label "Comparator:"
    :placeholder "metric value comparator: >, =>, <, <="}
   {:key :threshold
    :id-base "threshol-watcher_"
    :label "Threshold:"
    :placeholder "threshold for metric value"
    :format #(js/parseFloat %)}
   {:key :freq
    :id-base "freq-watcher_"
    :label "Frequence:"
    :placeholder "check interval in seconds"
    :format #(js/parseInt %)}
   {:key :mailto
    :id-base "mailto-watcher_"
    :label "Mail to:"
    :placeholder "foo@mail.com,bar@mail.com"}
   {:key :subject
    :id-base "subject-watcher_"
    :label "Subject:"
    :placeholder "mail subject"}
   {:key :max-sent
    :id-base "max-warcher_"
    :label "Max sent:"
    :placeholder "maximum number of mails sent"
    :format #(js/parseInt %)}
   {:key :cooldown
    :id-base "cool-watcher_"
    :label "Cooldown:"
    :placeholder "time to wait, in sec, before resending mails when max is reached"
    :format #(js/parseInt %)}])

(def format-fn
  (memoize 
   (fn [k] 
     (let [attr (->> watcher-attrs
                     (filter #(= k (:key %)))
                     first)]
       (if (nil? attr)
         identity
         (get attr :format identity))))))

(defn format-attr [k v]
  ((format-fn k) v))

(defn watcher-namer [topo-id watcher-idx watcher-name]
  (reagent/create-class
   {:component-did-mount 
    #(js/$ (fn []
             (.click 
              (js/$ (str "#edit-watcher_" watcher-idx "_" topo-id))
              (fn [e] 
                (.stopPropagation e)
                (.editable (js/$ (str "#name-watcher_" watcher-idx "_" topo-id)) 
                           "toggle")))
             (.editable 
              (js/$ (str "#name-watcher_" watcher-idx "_" topo-id))
              (clj->js {:url (fn [p] 
                               (dispatch [:set-watcher-attr
                                          (keyword topo-id) watcher-idx
                                          :name (.-value p)]))}))))
    :component-did-update
    (fn [this]
      (let [[_ topo-id watcher-idx watcher-name] (reagent/argv this)]
        (.editable 
         (js/$ (str "#name-watcher_" watcher-idx "_" topo-id)) 
         "setValue" watcher-name)))
    :reagent-render
    (fn [topo-id watcher-idx watcher-name]
      [:span
       [:a.collapser 
        {:id (str "name-watcher_" watcher-idx "_" topo-id) 
         :data-toggle "collapse"
         :data-type "text"
         :href (str "#collapse-watcher_" watcher-idx "_" topo-id) 
         :style {:color "inherit" :outline 0}}
        watcher-name]
       [:span {:id (str "edit-watcher_" watcher-idx "_" topo-id) 
               :style {:margin-left "10px" :cursor "pointer"}}
        [:i.glyphicon.glyphicon-pencil]]])}))

(defn watcher-inputer [topo-id watcher-idx attr value]
  (reagent/create-class
   {:component-did-mount
    (fn [this] ;; used to reset original value after canceling
      (let [[_ _ _ _ input-val] (reagent/argv this)
            input-elem (-> (reagent/dom-node this)
                           (#(aget (.-childNodes %) "1"))
                           (#(aget (.-childNodes %) "0")))]
        (set! (.-value input-elem) input-val)))
    :reagent-render
    (fn [topo-id watcher-idx {:keys [key id-base label format placeholder]} value]        
      (let [id (str id-base topo-id)]
        [:div.form-group
         [:label.control-label.col-sm-2 {:for id} label]
         [:div.col-sm-10
          [:input.form-control
           {:id id :type "text" :placeholder placeholder :value value
            :on-change #(dispatch [:set-watcher-attr
                                   (keyword topo-id) watcher-idx
                                   key (-> % .-target .-value)])}]]]))}))

(defn watcher-form [topo-id watcher-idx watcher]
  [:div.panel.panel-default
   [:div.panel-heading
    [:h4.panel-title     
     [watcher-namer topo-id watcher-idx (:name watcher)]     
     [:a
      {:id (str "delete-watcher_" topo-id) :style {:float "right"} :href "#"}
      [:i.glyphicon.glyphicon-trash]]]]
   [:div.pannel-collapse.collapse 
    {:id (str "collapse-watcher_" watcher-idx "_" topo-id)}
    [:div.panel-body
     [:form.form-horizontal
      (for [attr watcher-attrs]
        [watcher-inputer topo-id watcher-idx attr ((:key attr) watcher)])]]]])

(defn topology-modal [topo-id]
  (let [topology (subscribe [:topology topo-id])
        topo-watchers (subscribe [:editable-watchers topo-id])]
    (reagent/create-class
     {:component-did-mount
      (fn [this]
        (.on (js/$ (reagent/dom-node this))
             "hidden.bs.modal"
             (fn [] ;; will discard unsaved changes
               (dispatch-sync [:reset-editable-watchers
                               topo-id (:monitoring @topology)]))))
      :reagent-render
      (fn [topo-id]        
        [:div.modal.fade
         {:role "dialog" :id (:id @topology)}
         [:div.modal-dialog
          [:div.modal-content
           [:div.modal-header.text-center
            [:div.modal-title
             [:h3 (:name @topology)] 
             [:h5 (str "(" (:id @topology) ")")]]]
           [:div.modal-body.text-left
            [:div.panel-group
             (doall
              (map-indexed (partial watcher-form (:id @topology))
                           @topo-watchers))]
            [:div.form-group
             [:button.btn.btn-default.btn-sm.center-block
              {:type "button" :on-click #(dispatch [:add-watcher topo-id])} 
              [:i.glyphicon.glyphicon-plus] " Add Watcher"]]]
           [:div.modal-footer
            [:button.btn.btn-primary
             {:type "button" :data-dismiss "modal" 
              :on-click #(dispatch [:commit-watchers topo-id])} 
             "Register Watchers"]
            [:button.btn.btn-default {:type "button" :data-dismiss "modal"} 
             "Cancel"]]]]])})))

(defn modals-panel []
  (let [topo-ids (subscribe [:topologies/ids])]
    (fn []
      [:div (for [topo-id @topo-ids]
              [topology-modal topo-id])])))

(defn topology-row [topology]
  [:tr 
   [:td (:name topology)]
   [:td (:id topology)]
   [:td (if (:monitored? topology) 
          [:span.glyphicon.glyphicon-ok]
          [:span.glyphicon.glyphicon-remove])]
   [:td 
    [:button.btn.btn-default.btn-modal 
     {:type "button" :data-toggle "modal" :data-target (str "#" (:id topology))} 
     "Edit"] " "
    [:button.btn.btn-default
     {:type "button"} "Stop"]]])

(defn topologies-panel []
  (let [topologies (subscribe [:topologies])]
    (fn []
      [:tbody.text-left 
       (for [topology @topologies]
         [topology-row topology])])))

(defn main-view []
  [:div 
   [:table.table
    [:thead [:tr [:th "Name"] [:th "Id"] [:th "Monitored?"] [:th "Monitoring"]]]
    [topologies-panel]]
   [modals-panel]])

;; -- Event Handlers ----------------------------------------------------------

(register-handler
 :add-watcher
 (fn [db [_ topo-id]]
   (update-in db [:editing :watchers topo-id] conj {})))

(defn topo-index [db topo-id]
  (let [id (name topo-id)]
    (->> db :topologies (reduce #(if (= (:id %2) id) (reduced %1) (inc %1)) 0))))

(register-handler
 :commit-watchers
 (fn [db [_ topo-id]]
   (let [watchers (->> (-> db :editing :watchers topo-id)
                       (mapv (fn [w] 
                               (->> w
                                    (map (fn [[k v]] [k (format-attr k v)]))
                                    (into {})))))]
     (POST "/eldar/watcher/register"
           {:params {:topo-id (name topo-id) :watchers watchers}
            :format :json
            :response-format :json
            :keywords? true
            :handler #(dispatch [:commit-watchers-ok %1 topo-id watchers])
            :error-handler #(dispatch [:commit-watchers-bad %1])})
     (assoc db :committing? true))))

(register-handler
 :commit-watchers-ok
 (fn [db [_ resp topo-id watchers]]
   (-> db
       (assoc :committing? false)
       (assoc-in [:topologies (topo-index db (name topo-id)) :monitoring] watchers))))

(register-handler ;; TODO real error handler
 :commit-watchers-bad
 (fn [db [_ resp]]
   (.log js/console (str "ERROR ERROR ERROR " resp))
   (-> db
       (assoc :committing? false)
       )))

(register-handler
 :reset-editable-watchers
 (fn [db [_ topo-id watchers]]
   (-> db
       (assoc-in [:editing :watchers topo-id] watchers))))

(register-handler
 :set-watcher-attr
 (fn [db [_ topo-id watcher-idx attr val]]
   (update-in db [:editing :watchers topo-id watcher-idx] conj [attr val])))

(register-handler 
 :init-db
 (fn [db _]
   (GET "/eldar/watcher/status"
        {:response-format :json
         :keywords? true
         :handler #(dispatch [:init-db-ok %1])
         :error-handler #(dispatch [:init-db-bad %1])})
   db))

(register-handler
 :init-db-ok
 (fn [db [_ resp]]
   (reduce (fn [new-db topology]
             (-> new-db
                 (assoc-in [:editing :watchers (keyword (:id topology))] 
                           (:monitoring topology))))
           (assoc db :topologies resp)
           resp)))

(register-handler
 :init-db-bad
 (fn [db [_ resp]]
   (-> db
       (assoc :err-topologies true) ;; TODO real handler (displaying some error modal ?)
       (assoc :topologies []))))

;; -- Entry Point -------------------------------------------------------------

(defn init []
  (let [editable (-> js/$ .-fn .-editable .-defaults)]
    (set! (.-mode editable) "inline")
    (set! (.-emptytext editable) "Unnamed Watcher")))

(defn ^:export main []
  (init)
  (dispatch-sync [:init-db])
  (reagent/render [main-view]
                  (js/document.getElementById "app")))
