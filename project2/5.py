import gym
import random

# WEST = 0
# SOUTH = 1
# EAST = 2
# NORTH = 3
direction = ['W', 'S', 'E', 'N']

max_episodes = 10000
epsilon = 0.1
epsilon_decay = 0.00001  # 0.00001
discount = 0.99
learning_rate = 0.1

e3_q_dict = {
 0: [0.5338236196703878, 0.4188524128841576, 0.4214521074253643, 0.4202887467286201],
 1: [0.16533941162829421, 0.25453514094097507, 0.2267707538232437, 0.4980649422665737],
 2: [0.3927116334936275, 0.23459502515158986, 0.2482255571102012, 0.24631530836638818],
 3: [0.05791620803650599, 0.11572270980158564, 0.018506045556552657, 0.04482679523790882],
 4: [0.5452974405845362, 0.39809910227093354, 0.3797970247143436, 0.2909200915458718],
 5: [0, 0, 0, 0],
 6: [0.09217665217426468, 0.07696660194886411, 0.2919158012634401, 0.06408609559809819],
 7: [0, 0, 0, 0],
 8: [0.4190485776742091, 0.39509526660356925, 0.44173932622553364, 0.570640579018623],
 9: [0.4011505800107953, 0.6173145781758325, 0.45991847862761087, 0.39258812096346807],
 10: [0.5256801472878304, 0.31663164341049205, 0.34025991798305366, 0.3292330875007392],
 11: [0, 0, 0, 0],
 12: [0, 0, 0, 0],
 13: [0.3119515045997277, 0.30299167239451397, 0.7558737990781172, 0.5583985349030157],
 14: [0.6726993550057809, 0.8505625247598058, 0.6678588506155296, 0.661516879953081],
 15: [0, 0, 0, 0]}


def run_algorithm(env, q_dict):
    observation = env.reset()
    done = False
    c = 1
    global epsilon

    while not done:
        c += 1
        action = get_epsilon_greedy_action(q_dict[observation])
        prev_observation = observation
        observation, reward, done, info = env.step(action)
        set_q_value(prev_observation, observation, action, reward, q_dict)
        epsilon *= 1-epsilon_decay
    return reward


def initiate_q_dict(n):
    q_dict = {}
    for i in range(n):
        q_dict[i] = [0]*4
    return q_dict


def get_epsilon_greedy_action(q_values):
    random_nr = random.random()
    if 1-epsilon > random_nr and max(q_values) != 0:
        best_action = q_values.index(max(q_values))
        return best_action
    random_action = random.randint(0, 3)
    return random_action


def set_q_value(state, next_state, action, reward, q_dict):
    q_dict[state][action] += \
        learning_rate*(reward + discount*(max(e3_q_dict[next_state])) - q_dict[state][action])
    # Q(s_t, a_t) += a[r_t+1 + Y * (max(a) Q(s_t+1, a)) - Q(s_t, a_t)]


def print_q_values(q_dict):
    for i in range(4):
        for j in range(4):
            q_val = q_dict[i*4+j][3]
            print("{:>16}".format("{0:.2f}".format(q_val)), end="\t\t")
        print()
        for j in range(4):
            q_val1 = q_dict[i*4+j][0]
            q_val2 = q_dict[i*4+j][2]
            print("{:>10}".format("{0:.2f}".format(q_val1)), "{:>11}".format("{0:.2f}".format(q_val2)), end="\t")
        print()
        for j in range(4):
            q_val = q_dict[i*4+j][1]
            print("{:>16}".format("{0:.2f}".format(q_val)), end="\t\t")
        print("\n")
    print("\n\n\n")


def print_env(env):
    env.render()
    print()


def main():
    total_rewards = 0
    q_dict = initiate_q_dict(16)
    env = gym.make('FrozenLake-v0')
    episode = 0
    while episode < max_episodes:
        total_rewards += run_algorithm(env, q_dict)
        episode += 1
        if episode % 100 == 0:
            print("{:>5}".format(episode), "\t", "{:<6}".format(total_rewards/100))
            total_rewards = 0
        elif episode == 1:
            print("{:>5}".format(episode), "\t",  "{:<6}".format(total_rewards))

    print_q_values(q_dict)
    env.render()

main()
