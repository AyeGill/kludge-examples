(defproject breakout "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  
  :dependencies [[com.badlogicgames.gdx/gdx "1.7.2"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.7.2"]
                 [com.badlogicgames.gdx/gdx-box2d "1.7.2"]
                 [com.badlogicgames.gdx/gdx-box2d-platform "1.7.2"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-bullet "1.7.2"]
                 [com.badlogicgames.gdx/gdx-bullet-platform "1.7.2"
                  :classifier "natives-desktop"]
                 [com.badlogicgames.gdx/gdx-platform "1.7.2"
                  :classifier "natives-desktop"]
                 [org.clojure/clojure "1.7.0"]
                 [play-clj "1.0.0-SNAPSHOT"]]
  
  :source-paths ["src" "src-common"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [breakout.core.desktop-launcher]
  :main breakout.core.desktop-launcher)
