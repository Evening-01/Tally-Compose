package com.evening.tally.ext

import com.evening.tally.App


val Int.string get() = App.CONTEXT.getString(this)