from flask import Flask,jsonify,request
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

import json
import argparse
import sqlparse

task_sql = {}
#{'node_A': 'INSERT INTO table1 SELECT * FROM source_table_A;', 'node_B': 'INSERT INTO table2 SELECT * FROM source_table_B;', 'node_C': "UPDATE table3 SET column1 = 'new_value' WHERE condition_C;", 'node_D': 'SELECT * FROM table1 WHERE condition_D;', 'node_E': 'SELECT * FROM table2 WHERE condition_E;', 'node_F': 'INSERT INTO final_table SELECT * FROM result_table_D UNION ALL SELECT * FROM result_table_E;'}
dependency = {}
#{'node_A': [], 'node_B': [], 'node_C': [], 'node_D': ['node_A', 'node_B', 'node_C'], 'node_E': [], 'node_F': ['node_D', 'node_E']}
converted_sql = {}

edge_list = []

def read_dag(dag_data):
    # Extract tasks and dependencies
    #'id', 'data/desc', 'source/cell','target/cell

    for item in dag_data:
        if "source" in item : #edge item
            edge_list.append( (item['source']['cell'], item['target']['cell']) )
        else: #node item
            task_id = item['id']
            sql_code = item['data']['desc']
            task_sql[task_id] = sql_code

    for key in task_sql:
        dependency[key] = []

    for edge in edge_list:
        source = edge[0]
        target = edge[1]
        dependency[target].append(source)

def read_file(json_file_path):
    # Read the JSON file
    with open(json_file_path, 'r') as json_file:
        dag_data = json.load(json_file)

    # Extract tasks and dependencies
    dag_id = dag_data.get('dag_id', '')
    tasks = dag_data.get('tasks', [])
    for task in tasks:
        task_id = task.get('task_id', '')
        sql = task.get('sql_code',[])
        task_dependencies = task.get('dependencies', [])
        task_sql[task_id] = sql
        dependency[task_id] = task_dependencies

# Update node with parent node SQL
def mod_sql(task_id):
    dependencies_list = dependency.get(task_id, [])
    if dependencies_list != []: # Else code remains the same
        counter=0
        final_sql="WITH"
        for item in dependencies_list: # assume dependencies in correct order
            final_sql += " in"+str(counter)+ " as ( \n"
            final_sql +=  task_sql.get(item, "")
            final_sql +=  " \n ),"
            counter += 1
        final_sql = final_sql.rstrip(',') # remove tailing comma
        final_sql += " \n" + task_sql.get(task_id,"")
        task_sql[task_id] = final_sql


# Go through nodes in topological order
def convert_sql():
    converted_tasks = list()

    def convert_task(task_id):
        if task_id not in converted_tasks:
            dependencies_list = dependency.get(task_id, [])
            for item in dependencies_list:
                convert_task(item)
            sql_code = task_sql.get(task_id, "")
            mod_sql(task_id)
            converted_tasks.append(task_id)

    for task_id in task_sql.keys():
        convert_task(task_id)

    outnode = converted_tasks[-1]
    return task_sql.get(outnode)

# For format
def auto_indent_sql(sql_code):
    parsed = sqlparse.format(sql_code, reindent=True, keyword_case='upper')

    return parsed

def visualization():
    # Create a Graphviz Digraph object
    dot = graphviz.Digraph(format='png')
    global dependency

    # Add nodes to the graph
    for node in dependency.keys():
        dot.node(node)

    # Add edges to the graph
    for node, dependencies in dependency.items():
        for dep in dependencies:
            dot.edge(dep, node)

    # Save the graph visualization as a PNG file
    dot.render('dag_visualization', view=True)


@app.route('/sql', methods = ['POST'])
def post_route():
    if request.method == 'POST':
        data = request.get_json()
        #print(data)
        read_dag(data)
        #print(dependency)
        #print(task_sql)
        result = convert_sql()
        res = auto_indent_sql(result)
        #print('DAG received: "{data}"'.format(data=data))
        print('SQL converted: "{res}"'.format(res=res))
        return res


if __name__=='__main__':
    app.run()

