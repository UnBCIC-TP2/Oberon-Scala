# To use any remove script the first time, type into a powershell:
# Set-ExecutionPolicy RemoteSigned -Scope CurrentUser

Write-Host '[SCRIPT] Installing Scoop...'

try { 
	irm get.scoop.sh | iex 
} finally { 
	$buckets = 'java', 'main'
	foreach ($bucket in $buckets) {
		try { 
			Write-Host '[SCRIPT] Adding bucket $bucket...'
			scoop bucket add $bucket 
		} finally { 
			# Nothing here because its just a warning.
		}
	}

	try { scoop install git } finally {
		Write-Host '[SCRIPT] Updating Scoop...'
		scoop update git
		scoop update

		Write-Host 'Atualizando libs'
		
		
		$packages = 'liberica8-jdk', 'scala', 'sbt'

		foreach ($package in $packages) {
			try { scoop install $package } finally { scoop update $package }
		}
	}
	
	Write-Host ""
	scoop status
	
	Write-Host ""
	Write-Host "* SCOOP.SH IS A POWERFUL DEVELOPMENT KIT MANAGER (FOR WINDOWS) *"
	Write-Host ""
	Write-Host "E.g.:"
	Write-Host "scoop install nodejs"
	Write-Host "scoop install python"
	Write-Host "scoop install scala"
	Write-Host ""
	Write-Host "[DONE] Requirements installed. Just run the project."
	Write-Host ""
}