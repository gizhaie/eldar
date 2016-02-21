("<!DOCTYPE html>"
 [:html
  {:lang "en"}
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible", :content "IE=edge"}]
   [:meta
    {:name "viewport", :content "width=device-width, initial-scale=1"}]
   [:meta {:name "description", :content ""}]
   [:meta {:name "author", :content ""}]
   [:title "Eldar - Storm Topologies Monitoring"]
   "<!-- Bootstrap Core CSS -->"
   [:link {:href "css/bootstrap.min.css", :rel "stylesheet"}]
   "<!-- Custom CSS -->"
   [:link {:href "css/bootstrap-editable.css", :rel "stylesheet"}]
   [:style "\n      body {\n      padding-top: 70px;\n      }\n    "]
   "<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->"
   "<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->"
   "<!--[if lt IE 9]>\n        <script src=\"https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js\"></script>\n        <script src=\"https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js\"></script>\n        <![endif]-->"]
  [:body
   "<!-- Navigation -->"
   [:nav.navbar.navbar-inverse.navbar-fixed-top
    {:role "navigation"}
    [:div.container
     "<!-- Brand and toggle get grouped for better mobile display -->"
     [:div.navbar-header
      [:button.navbar-toggle
       {:type "button",
        :data-toggle "collapse",
        :data-target "#bs-example-navbar-collapse-1"}
       [:span.sr-only "Toggle navigation"]
       [:span.icon-bar]
       [:span.icon-bar]
       [:span.icon-bar]]
      [:a.navbar-brand {:href "/eldar/ui"} "Eldar"]]]
    "<!-- /.container -->"]
   "<!-- Page Content -->"
   [:div.container
    [:div.row
     [:div.col-lg-12.text-center
      [:div#app]
      "<!-- Topologies table -->"
      [:table.table
       [:thead
        [:tr
         [:th "Name"]
         [:th "Id"]
         [:th "Monitored ?"]
         [:th "Monitoring"]]]
       [:tbody
        [:tr.text-left
         [:td "topo_name"]
         [:td "topo_name-123"]
         [:td [:span.glyphicon.glyphicon-remove]]
         [:td
          [:button.btn.btn-default.btn-modal
           {:type "button",
            :data-toggle "modal",
            :data-target "#modal-template"}
           "Edit"]
          [:button.btn.btn-default {:type "button"} "Stop"]]]]]
      "<!-- Topologies modals -->"
      [:div
       "<!-- One topology modal -->"
       [:div#modal-template.modal.fade
        {:role "dialog"}
        [:div.modal-dialog
         [:div.modal-content
          [:div.modal-header
           [:button.close {:type "button", :data-dismiss "modal"} "×"]
           [:h3.modal-title "Topology Name (topology id)"]]
          [:div.modal-body.text-left
           [:div.panel-group
            [:div.panel.panel-default
             [:div.panel-heading
              [:h4.panel-title
               [:a#name-watcher_1
                {:data-toggle "collapse", :href "#collapse_1"}
                "Watcher 1"]
               [:a#edit-watcher_1
                {:style "margin-left: 10px", :href "#"}
                [:i.glyphicon.glyphicon-pencil]]
               [:a#delete-watcher_1
                {:style "float: right", :href "#"}
                [:i.glyphicon.glyphicon-trash]]]]
             [:div#collapse_1.panel-collapse.collapse
              [:div.panel-body
               [:form.form-horizontal
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "type-watcher_1"}
                  "Type:"]
                 [:div.col-sm-10
                  [:input#type-watcher_1.form-control
                   {:type "text", :placeholder "spouts or bolts"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "check-watcher_1"}
                  "Check:"]
                 [:div.col-sm-10
                  [:input#check-watcher_1.form-control
                   {:type "text",
                    :placeholder
                    "any or all or bolt_id_1,bolt_id_2 etc"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "metric-watcher_1"}
                  "Metric:"]
                 [:div.col-sm-10
                  [:input#metric-watcher_1.form-control
                   {:type "text",
                    :placeholder "metric to watch from Storm API"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "comp-watcher_1"}
                  "Comparator:"]
                 [:div.col-sm-10
                  [:input#comp-watcher_1.form-control
                   {:type "text",
                    :placeholder
                    "metric value comparator: >, =>, <, <="}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "thre-watcher_1"}
                  "Threshold:"]
                 [:div.col-sm-10
                  [:input#thre-watcher_1.form-control
                   {:type "text",
                    :placeholder "threshold for metric value"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "freq-watcher_1"}
                  "Frequence:"]
                 [:div.col-sm-10
                  [:input#freq-watcher_1.form-control
                   {:type "text",
                    :placeholder "check interval in seconds"}]]]
                [:div.form-group
                 "                               \n                                "
                 [:label.control-label.col-sm-2
                  {:for "mail-watcher_1"}
                  "Mail to:"]
                 [:div.col-sm-10
                  [:input#mail-watcher_1.form-control
                   {:type "text",
                    :placeholder "abc@mail.com,def@mail.com"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "subject-watcher_1"}
                  "Subject:"]
                 [:div.col-sm-10
                  [:input#subject-watcher_1.form-control
                   {:type "text", :placeholder "mail subject"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "max-watcher_1"}
                  "Max sent:"]
                 [:div.col-sm-10
                  [:input#max-watcher_1.form-control
                   {:type "text",
                    :placeholder "maximum number of mails sent"}]]]
                [:div.form-group
                 [:label.control-label.col-sm-2
                  {:for "cool-watcher_1"}
                  "Cooldown:"]
                 [:div.col-sm-10
                  [:input#cool-watcher_1.form-control
                   {:type "text",
                    :placeholder
                    "time to wait, in sec, before resending mails when max is reached"}]]]]]]]
            [:div.panel.panel-default
             [:div.panel-heading
              [:h4.panel-title
               [:a
                {:data-toggle "collapse", :href "#collapse_2"}
                "Watcher 2"]]]
             [:div#collapse_2.panel-collapse.collapse
              [:div.panel-body [:div.form-group]]]]
            "                        \n                    "]
           [:div.form-group
            [:button.btn.btn-default.btn-sm.center-block
             {:type "button"}
             [:i.glyphicon.glyphicon-plus]
             " Add Watcher\n                      "]]
           [:button.btn.btn-primary
            {:type "submit"}
            "Register Watchers"]]
          [:div.modal-footer
           [:button.btn.btn-default
            {:type "button", :data-dismiss "modal"}
            "Cancel"]]]
         "<!-- /.modal-content -->"]]]]]
    "<!-- /.row -->"]
   "<!-- /.container -->"
   "<!-- jQuery Version 1.11.1 -->"
   [:script {:src "js/jquery.js"}]
   "<!-- Bootstrap Core JavaScript -->"
   [:script {:src "js/bootstrap.min.js"}]
   "<!-- Custom js -->"
   [:script {:src "js/bootstrap-editable.min.js"}]
   [:script
    "      \n      $(function(){\n\n      $.fn.editable.defaults.mode = 'inline';\n      $('#name-watcher_1').editable({\n        title: 'Watcher Name',\n        toggle: 'manual'\n      });\n      $('#edit-watcher_1').click(function(e){    \n        e.stopPropagation();\n        $('#name-watcher_1').editable('toggle');\n      });\n\n      });\n    "]]])
