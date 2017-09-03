(ns google-chart-example.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def day
  (reagent/atom 3))

(def some-data
  (reagent/atom [["Day", "Clicks"],
                 [1 12000]
                 [2 35000]
                 [3 44000]]))

(defonce ready?
  (reagent/atom false))

(defonce initialize
  (do
    (js/google.charts.load (clj->js {:packages ["corechart"]}))
    (js/google.charts.setOnLoadCallback
      (fn google-visualization-loaded []
        (reset! ready? true)))))

(defn data-table [data]
  (cond
    (map? data) (js/google.visualization.DataTable. (clj->js data))
    (string? data) (js/google.visualization.Query. data)
    (seqable? data) (js/google.visualization.arrayToDataTable (clj->js data))))

(defn draw-chart [chart-type data options]
  [:div
   (if @ready?
     [:div
      {:ref
       (fn [this]
         (when this
           (.draw (new (aget js/google.visualization chart-type) this)
                  (data-table data)
                  (clj->js options))))}]
     [:div "Loading..."])])

(defn hello-world []
  [:div
   [:h1 "Google Chart Example"]
   [:button
    {:on-click
     (fn [e]
       (swap! day inc)
       (swap! some-data conj [@day (/ (rand-int 300000) @day)]))}
    "click me!"]

   [draw-chart
    "LineChart"
    @some-data
    {:title (str "Clicks as of day " @day)}]])

(reagent/render-component
  [hello-world]
  (js/document.getElementById "app"))

(defn on-js-reload [])
