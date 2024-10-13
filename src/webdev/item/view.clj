(ns webdev.item.view
  (:require [hiccup.page :refer [html5]]
            [hiccup.core :refer [html h]]))

(def header
  (html
   [:nav.navbar.has-shadow
    [:div.navbar-brand
     [:a.navbar-item
      [:img {:src "/images/cofee-logo.png"}]]
     [:div.navbar-burger.burger
      [:span] [:span] [:span]]]
    [:div.navbar-menu
     [:div.navbar-start
      [:div.navbar-item
       [:p "Bean Rosting Lisp"]]]]]))

(def head-html
  (html
   [:head
    [:title "Lispwana"]
    [:meta {:name :viewport
            :content "width=device-width,initial-scale=1"}]
    [:link {:href "/icons/css/fontawesome.css"
            :rel :stylesheet}]
    [:link {:href "/icons/css/brands.css"
            :rel :stylesheet}]
    [:link {:href "/icons/css/solid.css"
            :rel :stylesheet}]
    [:link {:href "/css/bulma/css/bulma.min.css"
            :rel :stylesheet}]]))

(def order-by
  (html
   [:div.level-right
    [:div.level-item "Order by"]
    [:div.level-item
     [:div.select
      [:select
       [:option "Publish Date"]
       [:option "Price"]
       [:option "Page"]]]]]))

(def search-item
  (html
   [:div.level-item.is-hidden-tablet-only
    [:div.field.has-addons
     [:p.control
      [:input.input {:type "text" :placeholder "Item name"}]]
     [:p.control
      [:button.button "Search"]]]]))

(def menu-item
  (html
   [:li
    [:a.is-active {:href "#"}
     [:span.icon [:i.fa-solid.fa-gauge]]
     "Todo List"]]))

(defn update-item-form [id checked]
  (html
   [:form {:method "POST" :action (str "/items/" id)}
    [:input.is-hidden {:type "hidden" :name "_method" :value "PUT"}]
    [:input.is-hidden {:type "hidden" :name "checked" :value (if checked "false" "true")}]
    (if checked
      [:button.button.is-primary {:type "submit"} "DONE"]
      [:button.button.is-warning {:type "submit"} "TODO"])]))
(defn todo-item [item]
  (html
   [:div.column.is-12-tablet.is-6.desktop.is-4-widescreen
    [:article.box
     [:div.media-content
      [:p.title.is-5.is-spaced.is-marginless (h (:name item))]
      [:p.subtitle.is-marginless (h (:description item))]
      [:div.content.is-small (h (:date_created item))]
      [:div.content.columns
       [:div.column
        (update-item-form (:id item) (:checked item))]
       [:div.column
        [:form {:method "POST" :action (str "/items/" (:id item))}
         [:input.is-hidden {:type "hidden" :name "_method" :value "DELETE"}]
         [:button.button.is-danger.is-outlined {:type "submit"} "Delete"]]]]]]]))

(defn new-item []
  (html
   [:div#new-item-form
    [:p.title.is-3 "New Item"]
    [:form {:method "POST" :action "/items"}
     [:div.field
      [:label.label {:for :name-input} "Name"]
      [:div.control
       [:input#name-input.input {:type "text" :placeholder "Item name" :name :name}]]
      [:p.help "Input a todo name"]]
     [:div.field
      [:label.label {:for :description-input} "Description"]
      [:div.control
       [:textarea#description-input.textarea {:placeholder "Item Description" :name :description}]]
      [:p.help "Input a description of the todo"]]
     [:div.field.is-grouped
      [:div.control
       [:button.button.is-primary {:type "submit"} "Save"]]
      [:div.control
       [:button.button.is-link.is-light "Cancel"]]]]]))

(defn items-page [items]
  (html5 {:lang :en}
         head-html
         [:body
          header
          [:section.section
           [:div.columns
            [:div.column.is-4-table.is-3-desktop.is-2-widescreen
             [:nav.menu
              [:p.menu-label "Menu"]
              [:ul.menu-list
               menu-item]]]
            [:div.column
             [:h1.title "TODO"]
             [:nav.level
              [:div.level-left
               [:div.level-item
                [:p.subtitle.is-5
                 [:strong (h (count items))] " items"]]
               [:p.level-item
                [:a.button.is-success.has-text-white {:href "#"} "New"]]
               search-item]
              order-by]
             [:div.box (new-item)]
             [:div.columns.is-multiline
              (for [item items]
                (todo-item item))]]]]]))
