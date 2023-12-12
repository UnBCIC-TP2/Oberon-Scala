#! /bin/bash

# Check if user wants to clear sdkman previous versions
echo "[CUIDADO] Deseja limpar qualquer versão prévia do SDKMAN?"
read -p "Recomendado para uma instalação limpa [y/N]? " choose

# If yes, remove any previous version of sdkman
if [ "$choose" = "y" ] || [ "$choose" = "Y" ]; 
then 
    sudo rm -rf ~/.sdkman 
fi

# Update or install the basic requirements
sudo apt install bash zip unzip curl -y

# Get the SDKMAN (easiest way to get sdk and sbt)
curl -s "https://get.sdkman.io" | bash

# Calls a new terminal with source sdk commands (required to run sdk command)
bash -c "source /home/$USER/.sdkman/bin/sdkman-init.sh; 
sdk install java 8.0.302-open;
sdk install scala;
sdk install sbt; 
sdk version;"

echo ""
echo "* SDKMAN IS A POWERFUL DEVELOPMENT KIT MANAGER *"
echo ""
echo "E.g.:"
echo "sdk install sbt"
echo "sdk current sbt"
echo "sdk list sbt"
echo ""
echo "[DONE] Requirements installed. Just run the project."

# Terminal update to run new sdk commands
bash