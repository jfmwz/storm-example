class storm {
	$storm_version = hiera('storm.version')
	$storm_binary = "${storm_version}.zip"
	$nimbus_host = hiera("storm.nimbus.host")
	$zookeeper_hosts = hiera("storm.zookeeper.servers")
	$supervisor_ports = hiera("storm.supervisor.slots.ports", none)
	$drpc_servers = hiera("storm.drpc.servers", none)
	
	include storm-zeromq
	include storm-supervisord
	
	package { 'unzip':
		ensure => installed,
	}
	
	exec { "storm-dowload":
		command => "wget -N https://dl.dropbox.com/u/133901206/$storm_binary",
		path => "/usr/bin/:/bin/",
		cwd => "/tmp/",
		unless => "test -f /tmp/$storm_binary",
		returns => ["0","1"],
	}
	
	exec { "storm-unpack":
		command => "unzip -o /tmp/$storm_binary -d /usr/share/",
		path => "/usr/bin/:/bin/",
		require => [File["storm-log"], Package["unzip"]],
	}
	
	file { "storm-share-symlink":
		path => "/usr/share/storm",
		ensure => link,
		target => "/usr/share/$storm_version",
		require => Exec["storm-unpack"],
	}
	
	file { "storm-bin-symlink":
		path => "/usr/bin/storm",
		ensure => link,
		target => "/usr/share/$storm_version/bin/storm",
		require => File["storm-share-symlink"],
	}
  
	group { "storm-group":
		name => "storm",
		ensure     => present,
	}
	user { "storm-user":
		name => "storm",
		ensure     => present,
		gid        => 'storm',
		shell      => '/bin/bash',
		home => "/home/storm",
		managehome => true,
	}
	
	file { "storm-log":
		path => "/var/log/storm",
		ensure => directory,
		owner => "storm",
		group => "storm",
		require => [Group["storm"],User["storm"]],
	}
	
	file { "storm-log-config":
		path => "/usr/share/storm/log4j/storm.log.properties",
		ensure => present,
		content => template("storm/storm.log.properties.erb"),
		require => File["storm-share-symlink"],
	}
	
	file { "storm-etc-config-dir":
		path => "/etc/storm/",
		ensure => directory,
		owner => "storm",
		group => "storm",
	}
	
	file { "storm-etc-config":
		path => "/etc/storm/storm.yaml",
		ensure => file,
		content => template("storm/storm.yaml.erb"),
		require => [File['storm-etc-config-dir'], File['storm-share-symlink']],
	}
	
	file { "storm-conf-symlink":
		path => "/usr/share/storm/conf/storm.yaml",
		ensure => link,
		target => "/etc/storm/storm.yaml",
		require => File["storm-etc-config"],
	}
	
}
