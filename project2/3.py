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
        learning_rate*(reward + discount*(max(q_dict[next_state])) - q_dict[state][action])
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
