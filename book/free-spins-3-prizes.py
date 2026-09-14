import ast
import pandas as pd
import matplotlib.pyplot as plt

data_str = "{810=1, 1810=1, 1570=1, 670=1, 550=1, 1920=1, 780=1, 1500=1, 610=1, 3460=1, 2000=1, 1290=1}"

data_str = data_str.replace("=", ":")
data_dict = ast.literal_eval(data_str)

expanded_data = []
for prize, count in data_dict.items():
    expanded_data.extend([prize] * count)

df = pd.DataFrame(expanded_data, columns=['Prize'])

plt.figure(figsize=(12, 6))
plt.hist(df['Prize'], bins=100, edgecolor='black', log=True, color='skyblue')

plt.title('Distribution of Prizes into Third Free Spin', fontsize=14)
plt.xlabel('Prize Amount', fontsize=12)
plt.ylabel('Frequency (Log Scale)', fontsize=12)
plt.grid(axis='y', alpha=0.4, linestyle='--')
plt.tight_layout()

plt.show()
