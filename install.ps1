# To use any remove script the first time, type into a powershell:
# Set-ExecutionPolicy RemoteSigned -Scope CurrentUser

# Scoop Install ()
irm get.scoop.sh | iex

# Java 8 dev kit
scoop bucket add java
scoop install liberica8-jdk

# Scala
scoop bucket add main
scoop install scala

# SBT
scoop install sbt

echo ""
echo "* SCOOP.SH IS A POWERFUL DEVELOPMENT KIT MANAGER (FOR WINDOWS) *"
echo ""
echo "E.g.:"
echo "scoop install nodejs"
echo "scoop install python"
echo "scoop install scala"
echo ""
echo "[DONE] Requirements installed. Just run the project."