def main():
    global inputs
    global outputs
    global transactions
    import pandas as pd
    inputs = pd.read_csv("inputs.csv", names=["id", "tx_id", "output_id"])
    outputs = pd.read_csv("outputs.csv", names=["id", "tx_id", "pk_id", "value"])
    transactions = pd.read_csv("transactions.csv", names=["id", "block_id", "is_coinbase"])
    global invalid_blocks
    invalid_blocks = set()
    find_invalid_blocks()
    find_invalid_coinbase()
    find_double_spend()
    # this block has many future references so we'll consider it valid
    invalid_blocks.discard(97423)
    #loop until we find no new blocks referencing invalid blocks
    while(find_invalid_ref_blocks()):
        pass
    print("List of invalid blocks:", invalid_blocks)
    remove_invalid_blocks(invalid_blocks)
    global accounts
    accounts = assign_accounts()
    global utxo_set
    utxo_set = gen_utxo_set()
    print("Number of existing utxos:", len(utxo_set))
    #cluster pk_ids
    joint_control()
    serial_control()
    richest_pk, holdings = most_btc()
    print("Addr of largest btc holdings is:", richest_pk, "with", holdings, "satoshis")
    print("Tx_id of largest send to this addr is:", int(outputs[outputs["pk_id"] == richest_pk].tx_id))

# find blocks where output != input + block reward
def find_invalid_blocks():
    global block_reward
    global invalid_blocks
    block_reward = 5000000000
    for i in range(1,100017):
        block_inputs = 0
        block_outputs = 0
        for tx_id in transactions[transactions["block_id"] == i].id.tolist():
            block_outputs += sum(outputs[outputs["tx_id"] == tx_id].value)
            for input in inputs[inputs["tx_id"] == tx_id].output_id.tolist():
                block_inputs += sum(outputs[outputs["id"] == input].value)
        if (block_outputs != block_reward + block_inputs):
            invalid_blocks.add(i)

# find coinbases with reward < 50 BTC
def find_invalid_coinbase():
    global invalid_blocks
    for row in transactions.itertuples():
        if (row[3] == 1):
            tx_id = row[1]
            output_total = sum(outputs[outputs["tx_id"] == tx_id].value)
            if (output_total < 5000000000):
                invalid_blocks.add(row[2])

# find outputs that were referenced more than once
def find_double_spend():
    global invalid_blocks
    for row in outputs.itertuples():
        id = row[1]
        output_ref_list = inputs[inputs["output_id"] == id].tx_id.tolist()
        if len(output_ref_list) > 1:
            #remove block with output that was referenced second
            for tx_id in output_ref_list[1:]:
                invalid_blocks.add(int(transactions[transactions["id"] == tx_id].block_id))

# find blocks referencing outputs from invalid blocks
def find_invalid_ref_blocks():
    global invalid_blocks
    new_invalid_blocks = set()
    found_new = False
    for bl in invalid_blocks:
        for tx_id in transactions[transactions["block_id"] == bl].id.tolist():
            for output_id in outputs[outputs["tx_id"] == tx_id].id.tolist():
                for input_id in inputs[inputs["output_id"] == output_id].tx_id.tolist():
                    for block_id in transactions[transactions["id"] == input_id].block_id.tolist():
                        if int(block_id) not in invalid_blocks:
                            new_invalid_blocks.add(block_id)
                            found_new = True
    invalid_blocks.update(new_invalid_blocks)
    return found_new

def remove_invalid_blocks(blocks):
    for block in blocks:
        for tx_id in transactions[transactions["block_id"] == block].id.tolist():
            for output in outputs[outputs["tx_id"] == tx_id].id.tolist():
                outputs.drop([output - 1], inplace=True)
            for input in inputs[inputs["tx_id"] == tx_id].id.tolist():
                inputs.drop([input - 1], inplace=True)
            transactions.drop([tx_id - 1], inplace=True)

def gen_utxo_set():
    utxo_set = set()
    max_value = 0
    max_value_id = 0
    for row in outputs.itertuples():
        if len(inputs[inputs["output_id"] == row[1]]) == 0:
            if row[4] > max_value:
                max_value = row[4]
                max_value_id = row[3]
            utxo_set.add(row[1])
    print("Largest utxo belongs to tx_id: ", max_value_id, "with value of: ", max_value)
    return utxo_set

def assign_accounts():
    accounts = []
    seen_pk_id = set()
    for row in outputs.itertuples():
        pk_id = set()
        pk_id.add(row[3])
        if (row[3] not in seen_pk_id):
            seen_pk_id.add(row[3])
            accounts.append(pk_id)
    return accounts

def serial_control():
    global accounts
    for row in transactions.itertuples():
        if (row[3] == 1):
            continue
        out = outputs[outputs["tx_id"] == row[1]].pk_id.tolist()
        # if theres only one output assign all input pks to this pk
        if (len(out) != 1):
            continue
        new_pk = out[0]
        for acc in accounts:
            if new_pk in acc:
                for inp in inputs[inputs["tx_id"] == row[1]].output_id.tolist():
                    pk_in = int(outputs[outputs["id"] == inp].pk_id)
                    for acc_ in accounts:
                        if pk_in in acc_:
                            acc.update(acc_)
                            accounts.remove(acc_)
                            break;

def joint_control():
    global accounts
    for row in transactions.itertuples():
        if (row[3] == 1):
            continue
        input_list = inputs[inputs["tx_id"] == row[1]].output_id.tolist()
        # if theres only one output assign all input pks to this pk
        if (len(input_list) == 1):
            continue
        new_pk = input_list[0]
        for acc in accounts:
            if new_pk in acc:
                for inp in input_list:
                    pk_in = int(outputs[outputs["id"] == inp].pk_id)
                    for acc_ in accounts:
                        if pk_in in acc_:
                            acc.update(acc_)
                            accounts.remove(acc_)
                            break;

def most_btc():
    global utxo_set
    most_btc = 0
    most_btc_acc = set()
    for acc in accounts:
        acc_balance = 0
        for pk in acc:
            txs = outputs[outputs["pk_id"] == pk].id.tolist()
            for tx in txs:
                if int(tx) in utxo_set:
                    acc_balance += int(outputs[outputs["id"] == tx].value)
        if acc_balance > most_btc:
            most_btc = acc_balance
            most_btc_acc = acc
    return min(most_btc_acc), most_btc

main()
