return buildWbsCtlCliModule(params);

function buildWbsCtlCliModule(params) {
    wpa_psk = [];
    wpa2_psk = [];
    wpa_wpa2_psk = [];
    dot_1x = [];
    wpa_ppsk = [];
    wpa2_ppsk = [];
    wpa_wpa2_ppsk = [];
    wpa3_sae = [];
    wpa2_psk_wpa3_sae = [];
    owe = [];
    cmds = [];
    encryptionMode = params.getString("encryptionMode");
    if (encryptionMode == "wpa-psk") {
        cmds = wpa_psk;
    } else if (encryptionMode == "wpa2-psk"){
        cmds = wpa2_psk;
    } else if (encryptionMode == "wpa_wpa2-psk"){
        cmds = wpa_wpa2_psk;
    } else if (encryptionMode == "Dot1x"){
        cmds = dot_1x;
    } else if (encryptionMode == "wpa-ppsk"){
        cmds = wpa_ppsk;
    } else if (encryptionMode == "wpa2-ppsk"){
        cmds = wpa2_ppsk;
    } else if (encryptionMode == "wpa_wpa2-ppsk"){
        cmds = wpa_wpa2_ppsk;
    } else if (encryptionMode == "wpa3-sae"){
        cmds = wpa3_sae;
    } else if (encryptionMode == "wpa2-psk_wpa3-sae"){
        cmds = wpa2_psk_wpa3_sae;
    } else if (encryptionMode == "owe"){
        cmds = owe;
    }

    return cmds;
}