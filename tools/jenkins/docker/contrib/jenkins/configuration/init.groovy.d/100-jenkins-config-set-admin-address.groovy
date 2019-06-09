#!/usr/bin/env groovy

//
// Should the email address be pulled in from a Config Map, a la 003-create-jobs.groovy?
// Perhaps...
// Url is already set up in the current jenkins image/build/scripts, so don't bother here.
// Only update the admin address.
//
import jenkins.model.*

jlc = JenkinsLocationConfiguration.get()
println "Update Location Configuration: current settings...."
println jlc.getUrl()
println jlc.getAdminAddress()
jlc.setAdminAddress("Marco Villeneuve <marco.1.villeneuve@gov.bc.ca>");
println "Update Location Configuration: updated settings...."
println jlc.getUrl()
println jlc.getAdminAddress()
