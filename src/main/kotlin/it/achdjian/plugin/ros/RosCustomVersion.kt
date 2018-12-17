package it.achdjian.plugin.ros

import it.achdjian.plugin.ros.settings.RosVersion

data class RosCustomVersion(val versions: MutableMap<String,String>){
    constructor() :  this(HashMap())

    fun contains(version: RosVersion) = versions.containsKey(version.name)
    fun remove(version: RosVersion) = versions.remove(version.name)

}