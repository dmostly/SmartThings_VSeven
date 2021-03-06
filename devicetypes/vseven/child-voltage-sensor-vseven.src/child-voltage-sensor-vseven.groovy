/**
 *  Child Voltage Sensor
 *
 *  Copyright 2017 Daniel Ogorchock
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2017-04-19  Dan Ogorchock  Original Creation
 *    2017-08-23  Allan (vseven) Added a generateEvent routine that gets info from the parent device.  This routine runs each time the value is updated which can lead to other modifications of the device.
 *    2018-06-02  Dan Ogorchock  Revised/Simplified for Hubitat Composite Driver Model
 *    2020-08-12  Allan (vseven) Quick hack for new app.  For testing only.
 *
 * 
 */
metadata {
	definition (name: "Child Voltage Sensor", namespace: "vseven", author: "Daniel Ogorchock", ocfDeviceType: "x.com.st.d.energymeter") {
		capability "Voltage Measurement"
		capability "Sensor"
		capability "Health Check"

		attribute "lastUpdated", "String"
	}
        
	tiles(scale: 2) {
		multiAttributeTile(name: "voltage", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.voltage", key: "PRIMARY_CONTROL") {
				attributeState("voltage", label: '${currentValue} ${unit}', unit: "mV", defaultState: true)
			}
 			tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'    Last updated ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
	}
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        // Update device
        sendEvent(name: name, value: value)
        // Update lastUpdated date and time
        def nowDay = new Date().format("MMM dd", location.timeZone)
        def nowTime = new Date().format("h:mm a", location.timeZone)
        sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def updated() {
	log.debug "updated()"
	initialize()
}

def installed() {
	log.debug "installed()"
	initialize()
}
def initialize() {
	sendEvent(name: "DeviceWatch-Enroll", value: JsonOutput.toJson([protocol: "cloud", scheme:"untracked"]), displayed: false)
	updateDataValue("EnrolledUTDH", "true")
}
