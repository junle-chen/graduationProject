import subprocess
import pandas as pd
import re
import os

# 设置JAR文件路径和命令行参数
jar_path = './out/artifacts/TCQ_LCQ_jar/TCQ_LCQ.jar'

# 数据集、算法索引的选项
dataSetIndices = [0, 1, 2]
algoIndices = list(range(9))

# 初始化CSV文件路径
output_csv_path = 'query_nums_lcq_tcq_sc.csv'

# 初始化CSV文件
if not os.path.exists(output_csv_path):
    df = pd.DataFrame(columns=["dataSetIndex", "algo_index", "query_nums", "time"])
    df.to_csv(output_csv_path, index=False)

def append_to_csv(data):
    try:
        df = pd.DataFrame(data, columns=["dataSetIndex", "algo_index", "query_nums", "time"])
        df.to_csv(output_csv_path, mode='a', header=False, index=False)
    except Exception as e:
        print(f"Error appending to CSV: {e}")

# 正则表达式模式，匹配 "query x running time: y ms"
pattern = re.compile(r"query (\d+) running time: (\d+)ms")

# 运行新增实验
for dataSetIndex in dataSetIndices:
    for algo_index in range(9):
        command = ['java', '-jar', jar_path, str(dataSetIndex), '3', '3', str(algo_index)]
        print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
        result = subprocess.run(command, capture_output=True, text=True)
        if result.returncode != 0:
            print(f"Error running command: {result.stderr}")
            continue  # 跳过错误的命令
        output = result.stdout.strip()
        print(f"Output: {output}")  # 打印输出以供调试
        for line in output.splitlines():
            match = pattern.match(line)
            if match:
                query_nums, running_time = match.groups()
                data = [[dataSetIndex, algo_index, query_nums, running_time]]
                append_to_csv(data)  # 追加到CSV
            else:
                print(f"Ignored line: {line}")  # 输出未匹配的行

print(f'实验结果已实时写入: {output_csv_path}')
