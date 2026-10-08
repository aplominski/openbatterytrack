package dev.aplominski.openbatterytrack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform