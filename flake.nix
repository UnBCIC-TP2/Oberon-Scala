{
  description = "An implementation of the Oberon language in Scala";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs }: 
    let
      build_for = system:

        let
	  pkgs = import nixpkgs { inherit system; };
	in

        pkgs.stdenv.mkDerivation {
          name = "hello";
          src = self;
          buildInputs = [
            pkgs.oh-my-zsh
	    pkgs.zsh
            pkgs.jdk11
	    (pkgs.sbt.override { jre = pkgs.jdk11; })
	  ];
        };
    in {	
      packages.x86_64-linux.default = build_for "x86_64-linux";
      packages.x86_64-darwin.default = build_for "x86_64-darwin";
    };
}
