package com.github.loinguyencs.safeinternetaccesschecker.rule

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class MyRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "MyRuleSet"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                ConnectToInternetEffectRule(config),
            ),
        )
    }
}
