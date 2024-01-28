assert({"门店 test": 1234}."门店 test" == 1234)

a = {"门店 test": 1234, "a b c d": 'oopp'}
assert(a.'门店 test' == 1234)
assert(a."a b c d" == 'oopp')

