import re

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'r') as f:
    content = f.read()

vm_logic = """
    private val _referralStats = MutableStateFlow(Pair(0, 0))
    val referralStats: StateFlow<Pair<Int, Int>> = _referralStats.asStateFlow()

    fun fetchReferralStats(refCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stats = repository.getReferralStats(refCode)
            _referralStats.value = stats
        }
    }
"""

if "fetchReferralStats" not in content:
    content = re.sub(r'}\s*$', vm_logic + "\n}", content)

with open('app/src/main/java/com/example/viewmodel/MainViewModel.kt', 'w') as f:
    f.write(content)
