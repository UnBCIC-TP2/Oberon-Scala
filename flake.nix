{
<<<<<<< HEAD
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
	    pkgs.emacs
            pkgs.jdk11
	    (pkgs.sbt.override { jre = pkgs.jdk11; })
	  ];
        };
    in {	
      packages.x86_64-linux.default = build_for "x86_64-linux";
      packages.x86_64-darwin.default = build_for "x86_64-darwin";
    };
=======
  nixConfig.bash-prompt-suffix = "🌿 ";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.05";
  inputs.nixpkgs_old.url = "github:NixOS/nixpkgs/nixos-22.11";
  inputs.nixpkgs_unstable.url = "github:NixOS/nixpkgs/nixos-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, nixpkgs_old, nixpkgs_unstable, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      rec {
        pkgs = nixpkgs.legacyPackages.${system};
        pkgs_old = nixpkgs_old.legacyPackages.${system};
        pkgs_unstable = nixpkgs_unstable.legacyPackages.${system};
        devShells = {
          default = import ./shell.nix { inherit pkgs pkgs_old pkgs_unstable system; };
          withDebugTools = import ./shell.nix { inherit pkgs pkgs_old pkgs_unstable system; withDebugTools = true; };
          withHiddenDylibs = import ./shell.nix { inherit pkgs pkgs_old pkgs_unstable system; withHiddenDylibs = true; };
        };
      }
    );
>>>>>>> de97368 (adding nix support)
}
