Prequisites

Install puppet labs APT repos:

```
wget http://apt.puppetlabs.com/puppetlabs-release-precise.deb
sudo dpkg -i puppetlabs-release-precise.deb
sudo apt-get update
```

Install puppet 3.x:

```
sudo apt-get install puppet
```

Apply configuration:

```
sudo puppet apply site.pp --modulepath=./modules --hiera_config ./hiera.yaml --environment=standalone
```